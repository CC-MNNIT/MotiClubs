package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.use_case.ReplyUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.use_case.ViewUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostScreenViewModel @Inject constructor(
    private val application: Application,
    private val userUseCases: UserUseCases,
    private val viewUseCases: ViewUseCases,
    private val replyUseCases: ReplyUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "PostScreenViewModel"
    }

    var postNotificationModel by mutableStateOf(
        savedStateHandle.get<PostNotificationModel>(NavigationArgs.POST_ARG)
            ?: PostNotificationModel()
    )

    val userMap = mutableStateMapOf<Long, User>()
    var viewCount by mutableStateOf("-")
    val replyList = mutableListOf<Reply>()
    val replyMsg = mutableStateOf("")

    val showProgress = mutableStateOf(false)
    val loadingReplies = mutableStateOf(false)
    val showDialog = mutableStateOf(false)

    @OptIn(ExperimentalMaterialApi::class)
    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Collapsed),
            snackbarHostState = SnackbarHostState()
        )
    )

    private fun getUsers() {
        viewModelScope.launch {
            val users = userUseCases.getAllUsers().first()
            if (users is Resource.Error) {
                return@launch
            }

            users.d?.let { list -> list.forEach { user -> userMap[user.userID] = user } }
        }
    }

    private var getRepliesJob: Job? = null
    private var sendReplyJob: Job? = null
    private var viewPostJob: Job? = null
    private var getViewJob: Job? = null

    fun getReplies() {
        getRepliesJob?.cancel()
        getRepliesJob = replyUseCases.getReplies(postNotificationModel.postID).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    loadingReplies.value = true
                    resource.data?.let { list ->
                        replyList.clear()
                        replyList.addAll(list)
                    }
                }

                is Resource.Success -> {
                    loadingReplies.value = false
                    replyList.clear()
                    replyList.addAll(resource.data)
                }

                is Resource.Error -> {
                    loadingReplies.value = false
                    Toast.makeText(
                        application,
                        "Error ${resource.errCode}: ${resource.errMsg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendReply(to_uid: Long) {
        showProgress.value = true
        showDialog.value = true

        sendReplyJob?.cancel()
        sendReplyJob = replyUseCases.sendReply(
            Reply(
                postNotificationModel.postID,
                application.getUserID(),
                to_uid,
                replyMsg.value,
                System.currentTimeMillis()
            )
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    replyMsg.value = ""
                    showDialog.value = false
                    showProgress.value = false
                    getReplies()
                }

                is Resource.Error -> {
                    showDialog.value = false
                    showProgress.value = false
                    Toast.makeText(application, "Error: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun viewPost() {
        viewPostJob?.cancel()
        viewPostJob =
            viewUseCases.addViews(View(postNotificationModel.userID, postNotificationModel.postID))
                .onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> resource.data?.let { list ->
                            viewCount = list.size.toString()
                        }

                        is Resource.Success -> viewCount = resource.data.size.toString()
                        is Resource.Error -> Log.d(
                            TAG,
                            "viewPost: ${resource.errCode} : ${resource.errMsg}"
                        )
                    }
                }.launchIn(viewModelScope)
    }

    private fun getViews() {
        getViewJob?.cancel()
        getViewJob = viewUseCases.getViews(postNotificationModel.postID).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { list -> viewCount = list.size.toString() }
                is Resource.Success -> viewCount = resource.data.size.toString()
                is Resource.Error -> Log.d(TAG, "getViews: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    init {
        viewPost()
        getViews()
        getReplies()
        getUsers()
    }
}
