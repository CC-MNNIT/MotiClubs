@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.Repository
import com.mnnit.moticlubs.data.network.Success
import com.mnnit.moticlubs.data.network.model.ClubNavModel
import com.mnnit.moticlubs.data.network.model.PostDto
import com.mnnit.moticlubs.data.network.model.PushPostModel
import com.mnnit.moticlubs.data.network.model.UserClubDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val imageReplacerMap = mutableMapOf<String, String>()
    val postsList = mutableStateListOf<PostDto>()
    val clubNavModel by mutableStateOf(savedStateHandle.get<ClubNavModel>("club") ?: ClubNavModel())
    val loadingPosts = mutableStateOf(false)

    val isPreviewMode = mutableStateOf(false)
    val showGuidanceDialog = mutableStateOf(false)

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
    var isAdmin = false

    fun clearEditor() {
        postMsg.value = TextFieldValue("")
        imageReplacerMap.clear()
        editMode.value = false
        showProgress.value = false
    }

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

    private fun fetchSubscriberCount() {
        viewModelScope.launch {
            val clubID = clubNavModel.clubId
            val response = repository.getSubscribersCount(application, clubID)
            if (response is Success) {
                subscriberCount.value = response.obj.count
                Log.d("TAG", "fetchSubscriberCount: ${response.obj.count}")
            }
        }
    }

    fun subscribeToClub(appViewModel: AppViewModel, subscribe: Boolean) {
        showProgress.value = true
        viewModelScope.launch {
            val response = if (subscribe) {
                repository.subscribeClub(application, clubNavModel.clubId)
            } else {
                repository.unsubscribeClub(application, clubNavModel.clubId)
            }
            if (response is Success) {
                appViewModel.user.subscribed.apply {
                    if (subscribe) {
                        add(UserClubDto(clubNavModel.clubId))
                    } else {
                        removeIf { it.clubID == clubNavModel.clubId }
                    }
                }
                subscribed.value = appViewModel.user.subscribed.any { it.clubID == clubNavModel.clubId }

                fetchSubscriberCount()
                Toast.makeText(application, if (subscribe) "Subscribed" else "Unsubscribed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(application, "${response.errCode}: Error could not process request", Toast.LENGTH_SHORT)
                    .show()
            }
            showProgress.value = false
        }
    }

    fun sendPost() {
        isPreviewMode.value = false

        var text = postMsg.value.text
        imageReplacerMap.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        viewModelScope.launch {
            val clubID = clubNavModel.clubId
            val channelID = clubNavModel.channel.id
            val response = repository.sendPost(
                application, PushPostModel(clubID, channelID, text, true)
            )

            if (response is Success) {
                Toast.makeText(application, "Posted", Toast.LENGTH_SHORT).show()
                clearEditor()
                fetchPostsList()
            } else {
                showProgress.value = false
                Toast.makeText(application, "${response.errCode}: Error posting msg", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updatePost() {
        isPreviewMode.value = false

        var text = postMsg.value.text
        imageReplacerMap.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        viewModelScope.launch {
            val response = repository.updatePost(application, postsList[editPostIdx.value].postID, text)
            if (response is Success) {
                Toast.makeText(application, "Updated", Toast.LENGTH_SHORT).show()
                clearEditor()
                fetchPostsList()
            } else {
                showProgress.value = false
                Toast.makeText(application, "${response.errCode}: Error updating msg", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deletePost() {
        progressText.value = "Deleting ..."
        showProgress.value = true
        if (delPostIdx.value < 0) return

        viewModelScope.launch {
            val response = repository.deletePost(
                application, postsList[delPostIdx.value].postID, clubNavModel.channel.id
            )
            if (response is Success) {
                showProgress.value = false
                Toast.makeText(application, "Post deleted", Toast.LENGTH_SHORT).show()
                fetchPostsList()
            } else {
                showProgress.value = false
                Toast.makeText(application, "${response.errCode}: Error deleting post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    init {
        fetchPostsList()
        fetchSubscriberCount()
    }
}
