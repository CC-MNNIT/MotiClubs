package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.use_case.ReplyUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.use_case.ViewUseCases
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val replyList = mutableStateListOf<Reply>()
    val replyMsg = mutableStateOf("")

    var pageEnded by mutableStateOf(false)
    var paging by mutableStateOf(false)
    private var postPage = 1

    val showProgress = mutableStateOf(false)
    val loadingReplies = mutableStateOf(false)
    val showDialog = mutableStateOf(false)

    val showDeleteDialog = mutableStateOf(false)
    val showConfirmationDeleteDialog = mutableStateOf(false)
    val replyDeleteItem = mutableStateOf(Reply())

    @OptIn(ExperimentalMaterialApi::class)
    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Collapsed),
            snackbarHostState = SnackbarHostState()
        )
    )

    private var getReplyJob: Job? = null
    private var getViewJob: Job? = null
    private var sendReplyJob: Job? = null
    private var deleteReplyJob: Job? = null
    private var viewPostJob: Job? = null

    fun getUser(userId: Long) {
        userUseCases.getUser(userId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { user -> userMap[user.userId] = user }
                is Resource.Success -> userMap[resource.data.userId] = resource.data

                is Resource.Error -> {
                    Log.d(TAG, "getUser: error fetching $userId; ${resource.errCode}: ${resource.errMsg}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getReplies(refresh: Boolean = true) {
        if (refresh) {
            postPage = 1
            pageEnded = false
            loadingReplies.value = true
        } else {
            if (pageEnded || paging) return
            paging = true
        }

        getReplyJob?.cancel()
        getReplyJob = replyUseCases.getReplies(postNotificationModel.postId, postPage).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    loadingReplies.value = true
                    resource.data?.let { list ->
                        when (refresh) {
                            true -> {
                                loadingReplies.value = true
                                replyList.clear()
                            }

                            else -> {
                                paging = true
                                replyList.removeIf { reply -> reply.pageNo == postPage }
                            }
                        }
                        replyList.addAll(list)
                    }
                }

                is Resource.Success -> {
                    when (refresh) {
                        true -> replyList.clear()
                        else -> replyList.removeIf { reply -> reply.pageNo == postPage }
                    }
                    when (resource.data.isEmpty()) {
                        true -> pageEnded = true
                        else -> postPage++
                    }
                    replyList.addAll(resource.data)
                    loadingReplies.value = false
                    paging = false
                }

                is Resource.Error -> {
                    loadingReplies.value = false
                    paging = false
                    Toast.makeText(
                        application,
                        "Error ${resource.errCode}: ${resource.errMsg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendReply() {
        showProgress.value = true
        showDialog.value = true

        sendReplyJob?.cancel()
        sendReplyJob = replyUseCases.sendReply(
            Reply(
                postNotificationModel.postId,
                application.getUserID(),
                replyMsg.value,
                pageNo = 1,
                System.currentTimeMillis(),
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

    fun deleteReply() {
        deleteReplyJob?.cancel()
        deleteReplyJob = replyUseCases.deleteReply(replyDeleteItem.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showDeleteDialog.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Reply deleted", Toast.LENGTH_SHORT).show()

                    replyList.removeIf { it.time == replyDeleteItem.value.time }
                    showDeleteDialog.value = false
                    replyDeleteItem.value = Reply()
                }

                is Resource.Error -> {
                    showDeleteDialog.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun viewPost() {
        viewPostJob?.cancel()
        viewPostJob =
            viewUseCases.addViews(View(postNotificationModel.userId, postNotificationModel.postId))
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
        getViewJob = viewUseCases.getViews(postNotificationModel.postId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { list -> viewCount = list.size.toString() }
                is Resource.Success -> viewCount = resource.data.size.toString()
                is Resource.Error -> Log.d(TAG, "getViews: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    private fun registerReplyReceiver() {
        LocalBroadcastManager.getInstance(application)
            .registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        getReplies()
                    }
                },
                IntentFilter(Constants.REPLY_BROADCAST_ACTION)
            )
    }

    init {
        registerReplyReceiver()
        viewPost()
        getViews()
        getReplies()
    }
}
