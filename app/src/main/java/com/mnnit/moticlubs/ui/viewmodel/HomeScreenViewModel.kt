package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
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
import com.mnnit.moticlubs.domain.usecase.ChannelUseCases
import com.mnnit.moticlubs.domain.usecase.ClubUseCases
import com.mnnit.moticlubs.domain.usecase.UserUseCases
import com.mnnit.moticlubs.domain.util.PublishedList
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.applySorting
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.onResource
import com.mnnit.moticlubs.domain.util.populate
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.publishedStateMapOf
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setAuthToken
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

    private var onResumeLocked by publishedStateOf(true)

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: $TAG: locked = $onResumeLocked")
        if (onResumeLocked) {
            onResumeLocked = false
            return
        }

        getUserHome(loadLocal = true)
        getAdmins()
    }

    var userModel by publishedStateOf(User())
    val adminList = publishedStateListOf<AdminUser>()
    val clubsList = publishedStateListOf<Club>()
    val channelMap = publishedStateMapOf<Long, PublishedList<Channel>>()

    var isFetchingAdmins by publishedStateOf(false)
    var isFetchingChannels by publishedStateOf(false)
    var isFetchingClubs by publishedStateOf(false)
    var editingEnabled = publishedStateOf(false)

    var showAddChannelDialog by publishedStateOf(false)
    var showProgressDialog by publishedStateOf(false)
    var progressMsg by publishedStateOf("")

    var eventChannel by publishedStateOf(Channel())
    var eventContact = publishedStateOf("")
    var inputChannelName by publishedStateOf("")
    var inputChannelPrivate by mutableIntStateOf(0)

    private var getUserJob: Job? = null
    private var getAdminJob: Job? = null
    private var getClubsChannelsJob: Job? = null
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
                    getUserHome()
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
        showAddChannelDialog = false
        progressMsg = "Adding"
        showProgressDialog = true

        addChannelJob?.cancel()
        addChannelJob = channelUseCases.addChannel(eventChannel)
            .onResource(
                onSuccess = {
                    channelMap.value[eventChannel.clubId]?.value?.removeIf { m ->
                        m.channelId == eventChannel.channelId
                    }
                    channelMap.value[eventChannel.clubId]?.value?.add(eventChannel)
                    showProgressDialog = false

                    Toast.makeText(application, "Added channel", Toast.LENGTH_SHORT).show()
                },
                onError = {
                    showProgressDialog = false
                    Toast.makeText(application, "${it.errCode}: ${it.errMsg}", Toast.LENGTH_SHORT).show()
                },
            ).launchIn(viewModelScope)
    }

    fun refreshAll() {
        Log.d(TAG, "refreshAll")

        isFetchingAdmins = true
        FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnSuccessListener {
            application.setAuthToken(it.token ?: "")

            getUserHome()
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

    private fun getUserHome(loadLocal: Boolean = false) {
        if (loadLocal) {
            Log.d(TAG, "getUser: loadLocal")
            viewModelScope.launch {
                userModel = repository.getUser(application.getUserId()) ?: User()
            }
            return
        }

        getUserJob?.cancel()
        getUserJob = userUseCases.getUser(application.getUserId()).onResource(
            onSuccess = {
                userModel = it
                eventContact.value = userModel.contact.ifEmpty { "None" }
            },
        ).launchIn(viewModelScope)

        getUserJob?.invokeOnCompletion { getClubsChannels() }
    }

    private fun getAdmins() {
        isFetchingAdmins = true
        getAdminJob?.cancel()
        getAdminJob = userUseCases.getAllAdmins().onResource(
            onSuccess = {
                adminList.apply(it)
                isFetchingAdmins = false
            },
            onError = { isFetchingAdmins = false },
        ).launchIn(viewModelScope)
    }

    private fun getClubsChannels(loadLocal: Boolean = false) {
        if (loadLocal) {
            Log.d(TAG, "getClubsChannels: loadLocal")
            viewModelScope.launch {
                populateClubsChannels(repository.getClubs(), repository.getAllChannels(userModel.userId))
            }
            return
        }

        getClubsChannelsJob?.cancel()
        getClubsChannelsJob = channelUseCases.getAllChannels(userModel.userId)
            .zip(clubUseCases.getClubs()) { r1, r2 -> transformResources(r1, emptyList(), r2, emptyList()) }
            .onEach { (channels, clubs) ->
                Log.d(TAG, "getClubsChannels: setting changes")
                populateClubsChannels(clubs, channels)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun populateClubsChannels(clubs: List<Club>, channels: List<Channel>) {
        val channelMembers = repository.getChannelsForMember(userModel.userId)
        clubsList.apply(clubs.applySorting(channelMembers))

        channels.populate(channelMap)
    }

    init {
        refreshAll()
    }
}
