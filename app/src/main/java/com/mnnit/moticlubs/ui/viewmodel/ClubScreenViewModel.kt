@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.Repository
import com.mnnit.moticlubs.data.network.Success
import com.mnnit.moticlubs.data.network.model.ClubNavModel
import com.mnnit.moticlubs.data.network.model.PostDto
import com.mnnit.moticlubs.data.network.model.PushPostModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val editMode = mutableStateOf(false)
    val editPostIdx = mutableStateOf(-1)
    val showEditDialog = mutableStateOf(false)

    val searchMode = mutableStateOf(false)
    val searchValue = mutableStateOf("")

    val postMsg = mutableStateOf(TextFieldValue(""))
    val postsList = mutableStateListOf<PostDto>()
    var clubNavModel by mutableStateOf(savedStateHandle.get<ClubNavModel>("club") ?: ClubNavModel())
    val loadingPosts = mutableStateOf(false)

    val isPreviewMode = mutableStateOf(false)

    val inputLinkName = mutableStateOf("")
    val inputLink = mutableStateOf("")
    val showLinkDialog = mutableStateOf(false)

    val progressText = mutableStateOf("Loading ...")
    val showProgress = mutableStateOf(false)
    val showDialog = mutableStateOf(false)
    val showSubsDialog = mutableStateOf(false)

    val showDelPostDialog = mutableStateOf(false)
    val delPostIdx = mutableStateOf(-1)

    val subscribed = mutableStateOf(false)
    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Collapsed),
            snackbarHostState = SnackbarHostState()
        )
    )
    val scrollValue = mutableStateOf(0)
    val subscriberCount = mutableStateOf(0)

    fun fetchPostsList() {
        loadingPosts.value = true
        viewModelScope.launch {
            val clubID = clubNavModel.clubId
            val channelID = clubNavModel.channel.id
            val response = repository.getPostsFromClubChannel(application, clubID = clubID, channelID = channelID)
            if (response is Success) {
                postsList.clear()
                postsList.addAll(response.obj)
            }
            loadingPosts.value = false
        }
    }

    fun fetchSubscriberCount() {
        viewModelScope.launch {
            val clubID = clubNavModel.clubId
            val response = repository.getSubscribersCount(application, clubID)
            if (response is Success) {
                subscriberCount.value = response.obj.count
                Log.d("TAG", "fetchSubscriberCount: ${response.obj.count}")
            }
        }
    }

    fun subscribeToClub(clubID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.subscribeClub(application, clubID)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun unsubscribeToClub(clubID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.unsubscribeClub(application, clubID)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun sendPost(message: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val clubID = clubNavModel.clubId
            val channelID = clubNavModel.channel.id
            val response = repository.sendPost(
                application, PushPostModel(clubID, channelID, message, true)
            )

            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun updatePost(postID: Int, message: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.updatePost(application, postID, message)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun deletePost(postID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.deletePost(application, postID, clubNavModel.channel.id)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    init {
        fetchPostsList()
        fetchSubscriberCount()
    }
}
