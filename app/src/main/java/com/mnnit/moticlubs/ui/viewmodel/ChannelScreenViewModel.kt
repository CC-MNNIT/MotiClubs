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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.usecase.MemberUseCases
import com.mnnit.moticlubs.domain.usecase.PostUseCases
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.Constants.INPUT_POST_MESSAGE_SIZE
import com.mnnit.moticlubs.domain.util.NavigationArgs.CHANNEL_ARG
import com.mnnit.moticlubs.domain.util.NavigationArgs.CLUB_ARG
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserId
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.lengthInRange
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.publishedStateMapOf
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class ChannelScreenViewModel @Inject constructor(
    private val application: Application,
    private val memberUseCases: MemberUseCases,
    private val postUseCases: PostUseCases,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val TAG = "ClubScreenViewModel"
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getPostsList()
        }
    }

    private var onResumeLocked by publishedStateOf(true)

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: $TAG: locked: $onResumeLocked")
        if (onResumeLocked) {
            onResumeLocked = false
            return
        }
        getModels()
        getPostsList()
    }

    val clubId by mutableLongStateOf(savedStateHandle.getLongArg(CLUB_ARG))
    val channelId by mutableLongStateOf(savedStateHandle.getLongArg(CHANNEL_ARG))
    var userId by mutableLongStateOf(-1)

    var clubModel by publishedStateOf(Club())
    var channelModel by publishedStateOf(Channel())

    val eventPostMsg = publishedStateOf(TextFieldValue(""))
    val eventImageReplacerMap = publishedStateMapOf<String, String>()
    val eventUpdatePost = publishedStateOf(Post())
    val eventDeletePost = publishedStateOf(Post())

    val adminMap = publishedStateMapOf<Long, AdminUser>()
    val postsList = publishedStateListOf<Post>()
    val loadingPosts = publishedStateOf(false)
    val memberCount = mutableIntStateOf(-1)

    val editMode = publishedStateOf(false)
    val showEditDialog = publishedStateOf(false)

    val searchMode = publishedStateOf(false)
    val searchValue = publishedStateOf("")

    val isPreviewMode = publishedStateOf(false)
    val showGuidanceDialog = publishedStateOf(false)

    val inputLinkName = publishedStateOf("")
    val inputLink = publishedStateOf("")
    val showLinkDialog = publishedStateOf(false)

    val progressText = publishedStateOf("Loading ...")
    val showProgress = publishedStateOf(false)
    val showDialog = publishedStateOf(false)
    val showDelPostDialog = publishedStateOf(false)
    val showClearDraftDialog = publishedStateOf(false)

    val bottomSheetScaffoldState = publishedStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(
                initialValue = BottomSheetValue.Collapsed,
                density = Density(application),
            ),
            snackbarHostState = SnackbarHostState(),
        ),
    )
    val scrollValue = mutableIntStateOf(0)
    var isAdmin by publishedStateOf(false)

    private var pageEnded by publishedStateOf(false)
    private var postPage = 1

    private var crudPostJob: Job? = null
    private var getPostsJob: Job? = null

    private var getMembersJob: Job? = null

    fun clearEditor() {
        eventPostMsg.value = TextFieldValue("")
        eventImageReplacerMap.value.clear()
        editMode.value = false
        showProgress.value = false
    }

    private fun getModels() {
        viewModelScope.launch {
            channelModel = repository.getChannel(channelId)
            clubModel = repository.getClub(clubId)

            userId = application.getUserId()

            val list = repository.getAdmins()
            isAdmin = list.any { admin -> admin.userId == userId && admin.clubId == clubId }
            list.forEach { admin -> adminMap.value[admin.userId] = admin }

            getMembers()
        }
    }

    private fun getMembers() {
        if (channelModel.private == 0) {
            memberCount.intValue = -1
            return
        }

        getMembersJob?.cancel()
        getMembersJob = memberUseCases.getMembers(channelId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { list -> memberCount.intValue = list.size }

                is Resource.Success -> {
                    memberCount.intValue = resource.data.size
                    Log.d("TAG", "getMembers: ${memberCount.intValue}")
                }

                is Resource.Error -> Log.d("TAG", "getMembers: error: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsList(refresh: Boolean = true) {
        loadingPosts.value = true

        if (refresh) {
            postPage = 1
            pageEnded = false
        }

        if (pageEnded) {
            Log.d(TAG, "getPostsList: page ended")
            loadingPosts.value = false
            return
        }

        Log.d(TAG, "getPostsList: page: $postPage")
        getPostsJob?.cancel()
        getPostsJob = postUseCases.getPosts(channelId, postPage).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        when (refresh) {
                            true -> {
                                loadingPosts.value = true
                                postsList.value.clear()
                            }

                            else -> postsList.value.removeIf { post -> post.pageNo == postPage }
                        }
                        postsList.value.addAll(list)
                    }
                }

                is Resource.Success -> {
                    when (refresh) {
                        true -> postsList.value.clear()
                        else -> postsList.value.removeIf { post -> post.pageNo == postPage }
                    }
                    when (resource.data.isEmpty()) {
                        true -> pageEnded = true
                        else -> postPage++
                    }
                    postsList.value.addAll(resource.data)
                    loadingPosts.value = false
                }

                is Resource.Error -> {
                    loadingPosts.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun postLengthInRange(): Boolean {
        var text = eventPostMsg.value.text
        eventImageReplacerMap.value.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        return INPUT_POST_MESSAGE_SIZE.lengthInRange(text)
    }

    fun sendPost() {
        isPreviewMode.value = false

        var text = eventPostMsg.value.text
        eventImageReplacerMap.value.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        val channelID = channelId
        val time = System.currentTimeMillis()
        crudPostJob?.cancel()
        crudPostJob = postUseCases.sendPost(
            Post(time, channelID, time, pageNo = 1, text, userId),
            clubId,
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Posted", Toast.LENGTH_SHORT).show()

                    postsList.value.clear()
                    postsList.value.addAll(resource.data)
                    clearEditor()
                }

                is Resource.Error -> {
                    showProgress.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updatePost() {
        isPreviewMode.value = false

        var text = eventPostMsg.value.text
        eventImageReplacerMap.value.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        crudPostJob?.cancel()
        val post = eventUpdatePost.value.copy(message = text)
        crudPostJob = postUseCases.updatePost(post, clubId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Updated", Toast.LENGTH_SHORT).show()

                    postsList.value.replaceAll { p -> if (p.postId == post.postId) post else p }
                    clearEditor()
                }

                is Resource.Error -> {
                    showProgress.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deletePost() {
        if (eventDeletePost.value.postId == 0L) return
        progressText.value = "Deleting ..."
        showProgress.value = true

        crudPostJob?.cancel()
        crudPostJob = postUseCases.deletePost(eventDeletePost.value, clubId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Post deleted", Toast.LENGTH_SHORT).show()

                    postsList.value.removeIf { post -> post.postId == eventDeletePost.value.postId }
                    showProgress.value = false
                }

                is Resource.Error -> {
                    showProgress.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun registerPostReceiver() {
        with(LocalBroadcastManager.getInstance(application)) {
            unregisterReceiver(receiver)
            registerReceiver(receiver, IntentFilter(Constants.POST_BROADCAST_ACTION))
        }
    }

    init {
        registerPostReceiver()
        getModels()
        getPostsList()
    }
}
