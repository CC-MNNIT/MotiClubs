package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.usecase.ReplyUseCases
import com.mnnit.moticlubs.domain.usecase.UserUseCases
import com.mnnit.moticlubs.domain.usecase.ViewUseCases
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.onResource
import com.mnnit.moticlubs.domain.util.postRead
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.publishedStateMapOf
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostScreenViewModel @Inject constructor(
    private val application: Application,
    private val replyUseCases: ReplyUseCases,
    private val repository: Repository,
    private val userUseCases: UserUseCases,
    private val viewUseCases: ViewUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "PostScreenViewModel"
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: reply broadcast")
            getReplies()
        }
    }

    val postId by mutableLongStateOf(savedStateHandle.getLongArg(NavigationArgs.POST_ARG))
    var userId by mutableLongStateOf(-1)

    var postModel by publishedStateOf(Post())
    var channelModel by publishedStateOf(Channel())
    var clubModel by publishedStateOf(Club())
    var userModel by publishedStateOf(User())

    val userMap = publishedStateMapOf<Long, User>()
    var viewCount by publishedStateOf("-")
    val viewsList = publishedStateListOf<View>()
    val replyList = publishedStateListOf<Reply>()
    val replyMsg = publishedStateOf("")

    private var pageEnded by publishedStateOf(false)
    private var replyPage = 1

    val showProgress = publishedStateOf(false)
    val loadingReplies = publishedStateOf(false)
    val showDialog = publishedStateOf(false)

    val showDeleteDialog = publishedStateOf(false)
    val showConfirmationDeleteDialog = publishedStateOf(false)
    val showViewedUserDialog = publishedStateOf(false)
    val replyDeleteItem = publishedStateOf(Reply())

    val bottomSheetScaffoldState = publishedStateOf(
        BottomSheetScaffoldState(
            bottomSheetState = SheetState(
                initialValue = SheetValue.PartiallyExpanded,
                skipPartiallyExpanded = false,
            ),
            snackbarHostState = SnackbarHostState(),
        ),
    )

    private var getReplyJob: Job? = null
    private var getViewJob: Job? = null
    private var sendReplyJob: Job? = null
    private var deleteReplyJob: Job? = null
    private var viewPostJob: Job? = null

    fun getUser(userId: Long) {
        userMap.value[userId] = User()
        userUseCases.getUser(userId).onResource(
            onSuccess = { userMap.value[it.userId] = it },
        ).launchIn(viewModelScope)
    }

    fun getReplies(refresh: Boolean = true) {
        loadingReplies.value = true

        if (refresh) {
            replyPage = 1
            pageEnded = false
        }

        if (pageEnded) {
            Log.d(TAG, "getReplies: page ended")
            loadingReplies.value = false
            return
        }

        Log.d(TAG, "getReplies: page: $replyPage")
        getReplyJob?.cancel()
        getReplyJob = replyUseCases.getReplies(postId, replyPage).onResource(
            onSuccess = {
                when (refresh) {
                    true -> replyList.value.clear()
                    else -> replyList.value.removeIf { reply -> reply.pageNo == replyPage }
                }
                when (it.isEmpty()) {
                    true -> pageEnded = true
                    else -> replyPage++
                }
                replyList.value.addAll(it)
                loadingReplies.value = false
            },
            onError = {
                loadingReplies.value = false
                Toast.makeText(
                    application,
                    "Error ${it.errCode}: ${it.errMsg}",
                    Toast.LENGTH_SHORT,
                ).show()
            },
        ).launchIn(viewModelScope)
    }

    fun sendReply() {
        showProgress.value = true
        showDialog.value = true

        sendReplyJob?.cancel()
        sendReplyJob = replyUseCases.sendReply(
            Reply(
                postId,
                application.getUserId(),
                replyMsg.value,
                pageNo = 1,
                System.currentTimeMillis(),
            ),
        ).onResource(
            onSuccess = {
                replyMsg.value = ""
                showDialog.value = false
                showProgress.value = false
                getReplies()
            },
            onError = {
                showDialog.value = false
                showProgress.value = false
                Toast.makeText(application, "Error: ${it.errMsg}", Toast.LENGTH_SHORT).show()
            },
        ).launchIn(viewModelScope)
    }

    fun deleteReply() {
        showDeleteDialog.value = true

        deleteReplyJob?.cancel()
        deleteReplyJob = replyUseCases.deleteReply(replyDeleteItem.value).onResource(
            onSuccess = {
                Toast.makeText(application, "Reply deleted", Toast.LENGTH_SHORT).show()

                replyList.value.removeIf { reply -> reply.time == replyDeleteItem.value.time }
                showDeleteDialog.value = false
                replyDeleteItem.value = Reply()
            },
            onError = {
                showDeleteDialog.value = false
                Toast.makeText(application, "${it.errCode}: ${it.errMsg}", Toast.LENGTH_SHORT).show()
            },
        ).launchIn(viewModelScope)
    }

    private fun getModels() {
        viewModelScope.launch {
            postModel = repository.getPost(postId)
            userModel = repository.getUser(postModel.userId) ?: User()
            channelModel = repository.getChannel(postModel.channelId)
            clubModel = repository.getClub(channelModel.clubId)
            userId = application.getUserId()

            application.postRead(postModel.channelId, postId, true)
            getReplies()
            viewPost()
            getViews()
        }
    }

    private fun viewPost() {
        viewPostJob?.cancel()
        viewPostJob = viewUseCases.addViews(View(postModel.userId, postModel.postId))
            .onResource(
                onSuccess = {
                    viewCount = it.size.toString()
                    viewsList.apply(it)
                },
            )
            .launchIn(viewModelScope)
    }

    private fun getViews() {
        getViewJob?.cancel()
        getViewJob = viewUseCases.getViews(postModel.postId).onResource(
            onSuccess = {
                viewsList.apply(it)
                viewCount = it.size.toString()
            },
        ).launchIn(viewModelScope)
    }

    private fun registerReplyReceiver() {
        with(LocalBroadcastManager.getInstance(application)) {
            unregisterReceiver(receiver)
            registerReceiver(receiver, IntentFilter(Constants.REPLY_BROADCAST_ACTION))
        }
    }

    init {
        registerReplyReceiver()
        getModels()
    }
}
