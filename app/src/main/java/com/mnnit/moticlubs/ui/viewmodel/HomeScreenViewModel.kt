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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.use_case.ChannelUseCases
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getUserID
import com.mnnit.moticlubs.domain.util.setAuthToken
import com.mnnit.moticlubs.domain.util.setUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val channelUseCases: ChannelUseCases,
    private val clubUseCases: ClubUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    companion object {
        private const val TAG = "HomeScreenViewModel"
    }

    var user by mutableStateOf(User())
    val adminList = mutableStateListOf<Admin>()
    val clubsList = mutableStateListOf<Club>()
    val channelMap = mutableStateMapOf<Long, SnapshotStateList<Channel>>()

    var isFetchingAdmins by mutableStateOf(false)
    var isFetchingChannels by mutableStateOf(false)
    var isFetchingClubs by mutableStateOf(false)

    var showAddChannelDialog by mutableStateOf(false)
    var showUpdateChannelDialog by mutableStateOf(false)
    var showProgressDialog by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    var eventChannel by mutableStateOf(Channel())
    var inputChannelName by mutableStateOf("")
    var updateChannelName by mutableStateOf("")
    var updateChannelPrivate by mutableIntStateOf(0)

    private var getUserJob: Job? = null
    private var getAdminJob: Job? = null
    private var getClubJob: Job? = null
    private var getChannelsJob: Job? = null
    private var addChannelJob: Job? = null
    private var updateChannelJob: Job? = null
    private var deleteChannelJob: Job? = null

    fun addChannel() {
        addChannelJob?.cancel()
        addChannelJob = channelUseCases.addChannel(eventChannel).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showAddChannelDialog = false
                    progressMsg = "Adding"
                    showProgressDialog = true
                }

                is Resource.Success -> {
                    channelMap[eventChannel.clubId]?.removeIf { m -> m.channelId == eventChannel.channelId }
                    channelMap[eventChannel.clubId]?.add(eventChannel)
                    showProgressDialog = false

                    Toast.makeText(application, "Added channel", Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    showProgressDialog = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateChannel() {
        updateChannelJob?.cancel()
        updateChannelJob = channelUseCases.updateChannel(eventChannel).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showUpdateChannelDialog = false
                    progressMsg = "Updating"
                    showProgressDialog = true
                }

                is Resource.Success -> {
                    channelMap[eventChannel.clubId]?.replaceAll { item ->
                        if (item.channelId == eventChannel.channelId) {
                            eventChannel
                        } else {
                            item
                        }
                    }
                    showProgressDialog = false

                    Toast.makeText(application, "Channel Updated", Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    showProgressDialog = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteChannel() {
        deleteChannelJob?.cancel()
        deleteChannelJob = channelUseCases.deleteChannel(eventChannel).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showUpdateChannelDialog = false
                    progressMsg = "Deleting"
                    showProgressDialog = true
                }

                is Resource.Success -> {
                    channelMap[eventChannel.clubId]?.removeIf { m -> m.channelId == eventChannel.channelId }
                    showProgressDialog = false

                    Toast.makeText(application, "Channel Deleted", Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    showProgressDialog = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun refreshAll(tokenRefresh: Boolean = true) {
        Log.d(TAG, "refreshAll: tokenRefresh = $tokenRefresh")

        isFetchingAdmins = true
        FirebaseAuth.getInstance().currentUser?.getIdToken(tokenRefresh)?.addOnSuccessListener {
            application.setUserID(it.claims["userId"]?.toString()?.toLong() ?: -1)
            application.setAuthToken(it.token ?: "")

            getUser()
            getClubs()
            getChannels()
            getAdmins()
        }?.addOnCompleteListener {
            isFetchingAdmins = false
            isFetchingChannels = false
            isFetchingClubs = false
            if (!it.isSuccessful) {
                Toast.makeText(application, "Error refreshing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUser() {
        getUserJob?.cancel()
        getUserJob = userUseCases.getUser(application.getUserID(), false).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { user = it }
                is Resource.Success -> user = resource.data
                is Resource.Error -> Log.d(TAG, "getUser: error: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    private fun getAdmins() {
        isFetchingAdmins = true
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
                    isFetchingAdmins = true
                }

                is Resource.Success -> {
                    adminList.clear()
                    adminList.addAll(resource.data)
                    isFetchingAdmins = false
                }

                is Resource.Error -> {
                    Log.d(TAG, "getAdmins: failed: ${resource.errCode}: ${resource.errMsg}")
                    isFetchingAdmins = false
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getClubs() {
        isFetchingClubs = true
        getClubJob?.cancel()
        getClubJob = clubUseCases.getClubs().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        if (list.isNotEmpty()) {
                            clubsList.clear()
                            clubsList.addAll(list)
                        }
                    }
                    isFetchingClubs = true
                }

                is Resource.Success -> {
                    clubsList.clear()
                    clubsList.addAll(resource.data)
                    isFetchingClubs = false
                }

                is Resource.Error -> {
                    Log.d(TAG, "getClubs: error: ${resource.errCode}: ${resource.errMsg}")
                    isFetchingClubs = false
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getChannels() {
        isFetchingChannels = true
        getChannelsJob?.cancel()
        getChannelsJob = channelUseCases.getChannels().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        list.forEach { channel -> channelMap[channel.clubId] = mutableStateListOf() }
                        list.forEach { channel -> channelMap[channel.clubId]?.add(channel) }
                    }
                    isFetchingChannels = true
                }

                is Resource.Success -> {
                    resource.data.forEach { channel -> channelMap[channel.clubId] = mutableStateListOf() }
                    resource.data.forEach { channel -> channelMap[channel.clubId]?.add(channel) }
                    isFetchingChannels = false
                }

                is Resource.Error -> {
                    Log.d(TAG, "getChannels: error: ${resource.errCode}: ${resource.errMsg}")
                    isFetchingChannels = false
                }
            }
        }.launchIn(viewModelScope)
    }

    init {
        refreshAll(tokenRefresh = false)
    }
}
