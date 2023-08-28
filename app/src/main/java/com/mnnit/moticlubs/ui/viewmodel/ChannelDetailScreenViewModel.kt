package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.use_case.ChannelUseCases
import com.mnnit.moticlubs.domain.use_case.MemberUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ChannelDetailScreenViewModel @Inject constructor(
    private val application: Application,
    private val channelUseCases: ChannelUseCases,
    private val memberUseCases: MemberUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var channelModel by mutableStateOf(savedStateHandle.get<Channel>(NavigationArgs.CHANNEL_ARG) ?: Channel())
    val userModel by mutableStateOf(savedStateHandle.get<User>(NavigationArgs.USER_ARG) ?: User())

    var isFetching by mutableStateOf(false)

    val memberList = mutableStateListOf<Member>()

    private var getChannelJob: Job? = null
    private var getMemberJob: Job? = null

    fun refreshAll() {
        getChannel()
        getMembers()
    }

    private fun getChannel() {
        getChannelJob?.cancel()
        getChannelJob = channelUseCases.getChannel(channelModel.channelId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isFetching = true
                    resource.data?.let { channel -> channelModel = channel }
                }

                is Resource.Success -> {
                    channelModel = resource.data
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

    init {
        getChannel()
        getMembers()
    }
}
