package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.usecase.ChannelUseCases
import com.mnnit.moticlubs.domain.usecase.MemberUseCases
import com.mnnit.moticlubs.domain.usecase.UserUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.applySorting
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.onResource
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.publishedStateMapOf
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
import com.mnnit.moticlubs.domain.util.transformResources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelDetailScreenViewModel @Inject constructor(
    private val application: Application,
    private val channelUseCases: ChannelUseCases,
    private val memberUseCases: MemberUseCases,
    private val userUseCases: UserUseCases,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val TAG = "ChannelDetailScreenView"
    }

    private var onResumeLocked by publishedStateOf(true)

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: $TAG: locked: $onResumeLocked")
        if (onResumeLocked) {
            onResumeLocked = false
            return
        }
        refreshAll()
    }

    val channelId by mutableLongStateOf(savedStateHandle.getLongArg(NavigationArgs.CHANNEL_ARG))
    var userId by mutableLongStateOf(-1)

    var channelModel by publishedStateOf(Channel())
    var clubModel by publishedStateOf(Club())

    var isFetching by publishedStateOf(false)
    var isUpdating by publishedStateOf(false)
    var isAdmin by publishedStateOf(false)

    var progressMsg by publishedStateOf("")
    var removeMemberUserId by mutableLongStateOf(-1)

    var showMemberProgressDialog = publishedStateOf(false)
    var showRemoveConfirmationDialog = publishedStateOf(false)
    var showPrivateConfirmationDialog = publishedStateOf(false)
    var showUpdateChannelDialog = publishedStateOf(false)

    var updateChannelName by publishedStateOf("")
    var updateChannelPrivate by mutableIntStateOf(0)

    val memberList = publishedStateListOf<Member>()
    val adminList = publishedStateListOf<AdminUser>()
    val memberInfo = publishedStateMapOf<Long, User>()

    private var getMemberJob: Job? = null
    private var removeMemberJob: Job? = null

    fun refreshAll() {
        getModels()
        memberInfo.value.clear()
    }

    fun updateChannel() {
        progressMsg = "Updating"
        showUpdateChannelDialog.value = false
        isUpdating = true

        channelUseCases.updateChannel(
            channelModel.copy(name = updateChannelName, private = updateChannelPrivate),
        ).onResource(
            onSuccess = {
                showUpdateChannelDialog.value = false
                isUpdating = false
                channelModel = it
                resetUpdate()
                refreshAll()
            },
            onError = {
                resetUpdate()
                Toast.makeText(application, "${it.errCode}: ${it.errMsg}", Toast.LENGTH_LONG).show()
            },
        ).launchIn(viewModelScope)
    }

    fun deleteChannel(onSuccess: () -> Unit) {
        showUpdateChannelDialog.value = false
        progressMsg = "Deleting"
        isUpdating = true

        channelUseCases.deleteChannel(channelModel).onResource(
            onSuccess = {
                isUpdating = false
                onSuccess()
                Toast.makeText(application, "Channel Deleted", Toast.LENGTH_SHORT).show()
            },
            onError = {
                isUpdating = false
                Toast.makeText(application, "${it.errCode}: ${it.errMsg}", Toast.LENGTH_SHORT).show()
            },
        ).launchIn(viewModelScope)
    }

    fun resetUpdate() {
        updateChannelName = channelModel.name
        updateChannelPrivate = channelModel.private
        isUpdating = false
    }

    fun removeMember() {
        if (removeMemberUserId == -1L) {
            Log.d(TAG, "removeMember: Invalid selected member userId")
            return
        }

        showMemberProgressDialog.value = true
        removeMemberJob?.cancel()
        removeMemberJob = memberUseCases.removeMember(
            channelModel.clubId,
            channelId,
            removeMemberUserId,
        ).onResource(
            onSuccess = {
                removeMemberUserId = -1L
                showMemberProgressDialog.value = false
                refreshAll()
            },
            onError = {
                removeMemberUserId = -1L
                showMemberProgressDialog.value = false
                Toast.makeText(application, "${it.errCode} ${it.errMsg}", Toast.LENGTH_SHORT).show()
            },
        ).launchIn(viewModelScope)
    }

    private fun getModels() {
        viewModelScope.launch {
            channelModel = repository.getChannel(channelId)
            clubModel = repository.getClub(channelModel.clubId)
            userId = application.getUserId()

            updateChannelName = channelModel.name
            updateChannelPrivate = channelModel.private

            getMembers()
        }
    }

    private fun getMembers() {
        isFetching = true

        getMemberJob?.cancel()
        getMemberJob = memberUseCases.getMembers(channelId)
            .zip(userUseCases.getAllAdmins()) { resourceMember, resourceAdmins ->
                transformResources(resourceMember, emptyList(), resourceAdmins, emptyList()) {
                    Log.d(TAG, "getMembers: $it")
                }
            }
            .onEach { (members, admins) ->
                isFetching = false
                if (members.isEmpty() || admins.isEmpty()) {
                    Log.d(TAG, "getMembers: members or admins, either empty")
                    return@onEach
                }

                admins.forEach { admin -> memberInfo.value[admin.userId] = admin.getUser() }

                isAdmin = admins.any { admin ->
                    admin.userId == userId && admin.clubId == channelModel.clubId
                }

                adminList.apply(admins)
                memberList.apply(members.applySorting(admins, channelModel.clubId, memberInfo))
            }.launchIn(viewModelScope)
    }

    fun getUser(userId: Long) {
        memberInfo.value[userId] = User()
        userUseCases.getUser(userId).onResource(
            onSuccess = { memberInfo.value[it.userId] = it },
            onError = { Log.d(TAG, "getUser: err - ${it.errCode}: ${it.errMsg}") },
        ).launchIn(viewModelScope)
    }

    init {
        refreshAll()
    }
}
