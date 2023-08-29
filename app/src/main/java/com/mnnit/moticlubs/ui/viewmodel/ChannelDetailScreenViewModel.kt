package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.use_case.ChannelUseCases
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.MemberUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChannelDetailScreenViewModel @Inject constructor(
    private val application: Application,
    private val clubUseCases: ClubUseCases,
    private val channelUseCases: ChannelUseCases,
    private val memberUseCases: MemberUseCases,
    private val userUseCases: UserUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "ChannelDetailScreenView"
    }

    var clubModel by mutableStateOf(savedStateHandle.get<Club>(NavigationArgs.CLUB_ARG) ?: Club())
    var channelModel by mutableStateOf(savedStateHandle.get<Channel>(NavigationArgs.CHANNEL_ARG) ?: Channel())
//    val userModel by mutableStateOf(savedStateHandle.get<User>(NavigationArgs.USER_ARG) ?: User())

    var showPrivateConfirmationDialog = mutableStateOf(false)
    var isFetching by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var isAdmin by mutableStateOf(false)

    var updateChannelName by mutableStateOf(channelModel.name)
    var updateChannelPrivate by mutableIntStateOf(channelModel.private)

    val memberList = mutableStateListOf<Member>()
    val adminList = mutableStateListOf<Admin>()
    val memberInfo = mutableStateMapOf<Long, User>()

    private var getChannelJob: Job? = null
    private var getMemberJob: Job? = null
    private var getAdminJob: Job? = null

    fun refreshAll() {
        getChannel()
        getMembers()
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

    private fun getChannel() {
        getChannelJob?.cancel()
        getChannelJob = channelUseCases.getChannel(channelModel.channelId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isFetching = true
                    resource.data?.let { channel ->
                        channelModel = channel
                        resetUpdate()
                    }
                }

                is Resource.Success -> {
                    channelModel = resource.data
                    resetUpdate()
                    isFetching = false
                }

                is Resource.Error -> {
                    isFetching = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getMembers() {
        if (channelModel.private == 0) {
            memberList.clear()
            return
        }

        getMemberJob?.cancel()
        getMemberJob = memberUseCases.getMembers(channelModel.channelId).onEach { resource ->
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
