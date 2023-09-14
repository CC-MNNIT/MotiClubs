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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.ChannelUseCases
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.setAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val channelUseCases: ChannelUseCases,
    private val clubUseCases: ClubUseCases,
    private val userUseCases: UserUseCases,
    private val repository: Repository,
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val TAG = "HomeScreenViewModel"
    }

    private var onResumeLocked by mutableStateOf(true)

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: $TAG: locked = $onResumeLocked")
        if (onResumeLocked) {
            onResumeLocked = false
            return
        }

        getUser(loadLocal = true)
        getClubs(loadLocal = true)
        getChannels(loadLocal = true)
        getAdmins()
    }

    var userModel by mutableStateOf(User())
    val adminList = mutableStateListOf<AdminUser>()
    val clubsList = mutableStateListOf<Club>()
    val channelMap = mutableStateMapOf<Long, SnapshotStateList<Channel>>()

    var isFetchingAdmins by mutableStateOf(false)
    var isFetchingChannels by mutableStateOf(false)
    var isFetchingClubs by mutableStateOf(false)
    var editingEnabled = mutableStateOf(false)

    var showAddChannelDialog by mutableStateOf(false)
    var showProgressDialog by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    var eventChannel by mutableStateOf(Channel())
    var eventContact = mutableStateOf("")
    var inputChannelName by mutableStateOf("")
    var inputChannelPrivate by mutableIntStateOf(0)

    private var getUserJob: Job? = null
    private var getAdminJob: Job? = null
    private var getClubJob: Job? = null
    private var getChannelsJob: Job? = null
    private var addChannelJob: Job? = null
    private var updateUserJob: Job? = null

    fun updateProfilePic(url: String, onResponse: () -> Unit, onFailure: () -> Unit) {
        updateUserJob?.cancel()
        updateUserJob = userUseCases.updateUser(userModel.copy(avatar = url)).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    userModel = resource.data
                    onResponse()
                }

                is Resource.Error -> {
                    Log.d(TAG, "updateProfilePic: ${resource.errCode}: ${resource.errMsg}")
                    onFailure()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateContactInfo() {
        val contact = eventContact.value.ifEmpty { "None" }

        showProgressDialog = true
        progressMsg = "Updating"
        updateUserJob?.cancel()
        updateUserJob = userUseCases.updateUser(userModel, contact).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    showProgressDialog = false
                    getUser()
                }

                is Resource.Error -> {
                    showProgressDialog = false
                    eventContact.value = userModel.contact.ifEmpty { "None" }
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

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

    fun refreshAll() {
        Log.d(TAG, "refreshAll")

        isFetchingAdmins = true
        FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnSuccessListener {
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

    private fun getUser(loadLocal: Boolean = false) {
        if (loadLocal) {
            Log.d(TAG, "getUser: loadLocal")
            viewModelScope.launch {
                userModel = repository.getUser(application.getUserId()) ?: User()
            }
            return
        }

        getUserJob?.cancel()
        getUserJob = userUseCases.getUser(application.getUserId()).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let {
                    userModel = it
                    eventContact.value = userModel.contact.ifEmpty { "None" }
                }

                is Resource.Success -> {
                    userModel = resource.data
                    eventContact.value = userModel.contact.ifEmpty { "None" }
                }

                is Resource.Error -> Log.d(TAG, "getUser: error: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    private fun getAdmins() {
        isFetchingAdmins = true
        getAdminJob?.cancel()
        getAdminJob = userUseCases.getAllAdmins().onEach { resource ->
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

    private fun getClubs(loadLocal: Boolean = false) {
        if (loadLocal) {
            Log.d(TAG, "getClubs: loadLocal")
            viewModelScope.launch {
                val list = repository.getClubs()
                clubsList.clear()
                clubsList.addAll(list)
            }
            return
        }

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

    private fun getChannels(loadLocal: Boolean = false) {
        if (loadLocal) {
            Log.d(TAG, "getChannels: loadLocal")
            viewModelScope.launch {
                val list = repository.getAllChannels()
                list.forEach { channel -> channelMap[channel.clubId] = mutableStateListOf() }
                list.forEach { channel -> channelMap[channel.clubId]?.add(channel) }
            }
            return
        }

        isFetchingChannels = true
        getChannelsJob?.cancel()
        getChannelsJob = channelUseCases.getAllChannels().onEach { resource ->
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
        refreshAll()
    }
}
