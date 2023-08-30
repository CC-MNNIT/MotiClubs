package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Density
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.MemberUseCases
import com.mnnit.moticlubs.domain.use_case.PostUseCases
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.NavigationArgs.CHANNEL_ARG
import com.mnnit.moticlubs.domain.util.NavigationArgs.CLUB_ARG
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class ChannelScreenViewModel @Inject constructor(
    private val application: Application,
    private val memberUseCases: MemberUseCases,
    private val postUseCases: PostUseCases,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val TAG = "ClubScreenViewModel"
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getPostsList()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "onResume: $TAG")
        getModels()
        getPostsList()
    }

    val clubId by mutableLongStateOf(savedStateHandle.getLongArg(CLUB_ARG))
    val channelId by mutableLongStateOf(savedStateHandle.getLongArg(CHANNEL_ARG))
    var userId by mutableLongStateOf(-1)

    var clubModel by mutableStateOf(Club())
    var channelModel by mutableStateOf(Channel())

    val eventPostMsg = mutableStateOf(TextFieldValue(""))
    val eventImageReplacerMap = mutableMapOf<String, String>()
    val eventUpdatePost = mutableStateOf(Post())
    val eventDeletePost = mutableStateOf(Post())

    val adminMap = mutableStateMapOf<Long, AdminUser>()
    val postsList = mutableStateListOf<Post>()
    val loadingPosts = mutableStateOf(false)
    val memberCount = mutableIntStateOf(-1)

    val editMode = mutableStateOf(false)
    val showEditDialog = mutableStateOf(false)

    val searchMode = mutableStateOf(false)
    val searchValue = mutableStateOf("")

    val isPreviewMode = mutableStateOf(false)
    val showGuidanceDialog = mutableStateOf(false)

    val inputLinkName = mutableStateOf("")
    val inputLink = mutableStateOf("")
    val showLinkDialog = mutableStateOf(false)

    val progressText = mutableStateOf("Loading ...")
    val showProgress = mutableStateOf(false)
    val showDialog = mutableStateOf(false)
    val showDelPostDialog = mutableStateOf(false)
    val showClearDraftDialog = mutableStateOf(false)

    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(
                initialValue = BottomSheetValue.Collapsed,
                density = Density(application),
            ),
            snackbarHostState = SnackbarHostState()
        )
    )
    val scrollValue = mutableIntStateOf(0)
    var isAdmin by mutableStateOf(false)

    var pageEnded by mutableStateOf(false)
    var paging by mutableStateOf(false)
    private var postPage = 1

    private var crudPostJob: Job? = null
    private var getPostsJob: Job? = null

    private var getMembersJob: Job? = null

    fun clearEditor() {
        eventPostMsg.value = TextFieldValue("")
        eventImageReplacerMap.clear()
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
            list.forEach { admin -> adminMap[admin.userId] = admin }

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
        if (refresh) {
            postPage = 1
            pageEnded = false
            loadingPosts.value = true
        } else {
            if (pageEnded || paging) return
            paging = true
        }

        getPostsJob?.cancel()
        getPostsJob = postUseCases.getPosts(channelId, postPage).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        when (refresh) {
                            true -> {
                                loadingPosts.value = true
                                postsList.clear()
                            }

                            else -> {
                                paging = true
                                postsList.removeIf { post -> post.pageNo == postPage }
                            }
                        }
                        postsList.addAll(list)
                    }
                }

                is Resource.Success -> {
                    when (refresh) {
                        true -> postsList.clear()
                        else -> postsList.removeIf { post -> post.pageNo == postPage }
                    }
                    when (resource.data.isEmpty()) {
                        true -> pageEnded = true
                        else -> postPage++
                    }
                    postsList.addAll(resource.data)
                    loadingPosts.value = false
                    paging = false
                }

                is Resource.Error -> {
                    loadingPosts.value = false
                    paging = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendPost() {
        isPreviewMode.value = false

        var text = eventPostMsg.value.text
        eventImageReplacerMap.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        val channelID = channelId
        val time = System.currentTimeMillis()
        crudPostJob?.cancel()
        crudPostJob = postUseCases.sendPost(
            Post(time, channelID, pageNo = 1, text, userId),
            clubId, 1
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Posted", Toast.LENGTH_SHORT).show()

                    postsList.clear()
                    postsList.addAll(resource.data)
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
        eventImageReplacerMap.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        crudPostJob?.cancel()
        val post = eventUpdatePost.value.copy(message = text)
        crudPostJob = postUseCases.updatePost(post, clubId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Updated", Toast.LENGTH_SHORT).show()

                    postsList.replaceAll { p -> if (p.postId == post.postId) post else p }
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

                    postsList.removeIf { post -> post.postId == eventDeletePost.value.postId }
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
