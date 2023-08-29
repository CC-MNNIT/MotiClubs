package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.ChannelUseCases
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.MemberUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelDetailScreenViewModel @Inject constructor(
    private val application: Application,
    private val clubUseCases: ClubUseCases,
    private val channelUseCases: ChannelUseCases,
    private val memberUseCases: MemberUseCases,
    private val userUseCases: UserUseCases,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "ChannelDetailScreenView"
    }

    val channelId by mutableLongStateOf(savedStateHandle.getLongArg(NavigationArgs.CHANNEL_ARG))

    var channelModel by mutableStateOf(Channel())
    var clubModel by mutableStateOf(Club())

    var showPrivateConfirmationDialog = mutableStateOf(false)
    var isFetching by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var isAdmin by mutableStateOf(false)

    var updateChannelName by mutableStateOf("")
    var updateChannelPrivate by mutableIntStateOf(0)

    val memberList = mutableStateListOf<Member>()
    val adminList = mutableStateListOf<AdminUser>()
    val memberInfo = mutableStateMapOf<Long, User>()

    private var getMemberJob: Job? = null
    private var getAdminJob: Job? = null

    fun refreshAll() {
        getModels()
        memberInfo.clear()
        getAdmins()
    }

    fun updateChannel() {
        channelUseCases.updateChannel(
            channelModel.copy(name = updateChannelName, private = updateChannelPrivate)
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> isUpdating = true
                is Resource.Success -> {
                    channelModel = resource.data
                    resetUpdate()
                    refreshAll()
                }

                is Resource.Error -> {
                    resetUpdate()
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun resetUpdate() {
        updateChannelName = channelModel.name
        updateChannelPrivate = channelModel.private
        isUpdating = false
    }

    private fun getModels() {
        viewModelScope.launch {
            channelModel = repository.getChannel(channelId)
            clubModel = repository.getClub(channelModel.clubId)

            updateChannelName = channelModel.name
            updateChannelPrivate = channelModel.private

            getMembers()
        }
    }

    private fun getMembers() {
        if (channelModel.private == 0) {
            memberList.clear()
            return
        }

        getMemberJob?.cancel()
        getMemberJob = memberUseCases.getMembers(channelId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        memberList.clear()
                        memberList.addAll(list)
                    }
                }

                is Resource.Success -> {
                    memberList.clear()
                    memberList.addAll(resource.data)
                }

                is Resource.Error -> {
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUser(userId: Long) {
        memberInfo[userId] = User()
        userUseCases.getUser(userId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { user -> memberInfo[user.userId] = user }
                }

                is Resource.Success -> {
                    val user = resource.data
                    memberInfo[user.userId] = user
                }

                is Resource.Error -> Log.d(TAG, "getUser: err - ${resource.errCode}: ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    private fun getAdmins() {
        getAdminJob?.cancel()
        getAdminJob = clubUseCases.getAdmins().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        if (list.isNotEmpty()) {
                            adminList.clear()
                            adminList.addAll(list)
                        }
                    }
                }

                is Resource.Success -> {
                    adminList.clear()
                    adminList.addAll(resource.data)
                    isAdmin = adminList.any { admin -> admin.userId == application.getUserID() }
                }

                is Resource.Error -> {
                    Log.d(TAG, "getAdmins: failed: ${resource.errCode}: ${resource.errMsg}")
                }
            }
        }.launchIn(viewModelScope)
    }

    init {
        refreshAll()
    }
}
