package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.PostUseCases
import com.mnnit.moticlubs.domain.use_case.SubscriberUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class ClubScreenViewModel @Inject constructor(
    private val application: Application,
    private val userUseCases: UserUseCases,
    private val clubUseCases: ClubUseCases,
    private val postUseCases: PostUseCases,
    private val subscriberUseCases: SubscriberUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val clubModel by mutableStateOf(savedStateHandle.get<Club>(NavigationArgs.CLUB_ARG) ?: Club())
    val channelModel by mutableStateOf(savedStateHandle.get<Channel>(NavigationArgs.CHANNEL_ARG) ?: Channel())
    val userModel by mutableStateOf(savedStateHandle.get<User>(NavigationArgs.USER_ARG) ?: User())

    val eventPostMsg = mutableStateOf(TextFieldValue(""))
    val eventImageReplacerMap = mutableMapOf<String, String>()
    val eventUpdatePost = mutableStateOf(Post())
    val eventDeletePost = mutableStateOf(Post())

    val adminMap = mutableStateMapOf<Int, User>()
    val postsList = mutableStateListOf<Post>()
    val loadingPosts = mutableStateOf(false)
    val subscriberList = mutableStateListOf<Subscriber>()

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
    val showSubsDialog = mutableStateOf(false)
    val showDelPostDialog = mutableStateOf(false)

    val userSubscribed = mutableStateOf(false)
    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Collapsed),
            snackbarHostState = SnackbarHostState()
        )
    )
    val scrollValue = mutableStateOf(0)
    var isAdmin by mutableStateOf(false)

    var pageEnded by mutableStateOf(false)
    var paging by mutableStateOf(false)
    private var postPage = 1

    private var crudPostJob: Job? = null
    private var getPostsJob: Job? = null

    private var getSubscribersJob: Job? = null
    private var subscriberJob: Job? = null

    fun clearEditor() {
        eventPostMsg.value = TextFieldValue("")
        eventImageReplacerMap.clear()
        editMode.value = false
        showProgress.value = false
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
        getPostsJob = postUseCases.getPosts(channelModel.channelID, postPage).onEach { resource ->
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
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getSubscribers() {
        getSubscribersJob?.cancel()
        getSubscribersJob = subscriberUseCases.getSubscribers(clubModel.clubID).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        subscriberList.clear()
                        subscriberList.addAll(list)
                    }
                }
                is Resource.Success -> {
                    subscriberList.clear()
                    subscriberList.addAll(resource.data)

                    userSubscribed.value = subscriberList.any { s ->
                        s.userID == userModel.userID && s.clubID == clubModel.clubID
                    }

                    Log.d("TAG", "fetchSubscribers: ${subscriberList.size}")
                }
                is Resource.Error -> {
                    Log.d("TAG", "fetchSubscribers: error: ${resource.errCode} : ${resource.errMsg}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAdmins() {
        viewModelScope.launch {
            val resource = clubUseCases.getAdmins(shouldFetch = false).first()
            if (resource is Resource.Error) {
                return@launch
            }

            resource.d?.let { list ->
                val admins = list.filter { admin -> admin.clubID == clubModel.clubID }
                isAdmin = admins.any { admin -> admin.userID == userModel.userID }

                admins.forEach { admin ->
                    val adminUserRes = userUseCases.getUser(admin.userID).first()

                    if (adminUserRes !is Resource.Error) {
                        adminUserRes.d?.let { adminUser -> adminMap[admin.userID] = adminUser }
                    }
                }
            }
        }
    }

    fun subscribeToClub(subscribe: Boolean) {
        showProgress.value = true

        subscriberJob?.cancel()
        subscriberJob = if (subscribe) {
            subscriberUseCases.subscribeClub(Subscriber(userModel.userID, clubModel.clubID)).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> showProgress.value = true
                    is Resource.Success -> {
                        showProgress.value = false
                        getSubscribers()
                        Toast.makeText(application, "Subscribed", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        showProgress.value = false
                        Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            subscriberUseCases.unsubscribeClub(Subscriber(userModel.userID, clubModel.clubID)).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> showProgress.value = true
                    is Resource.Success -> {
                        showProgress.value = false
                        getSubscribers()
                        Toast.makeText(application, "Unsubscribed", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        showProgress.value = false
                        Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun sendPost() {
        isPreviewMode.value = false

        var text = eventPostMsg.value.text
        eventImageReplacerMap.forEach { (key, value) ->
            text = text.replace(key.replace("\n", ""), value)
        }

        val channelID = channelModel.channelID
        val time = System.currentTimeMillis()
        crudPostJob?.cancel()
        crudPostJob = postUseCases.sendPost(
            Post(time, channelID, pageNo = 1, text, time, userModel.userID),
            clubModel.clubID, 1
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
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT)
                        .show()
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
        crudPostJob = postUseCases.updatePost(post).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Updated", Toast.LENGTH_SHORT).show()

                    postsList.replaceAll { p -> if (p.postID == post.postID) post else p }
                    clearEditor()
                }
                is Resource.Error -> {
                    showProgress.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deletePost() {
        if (eventDeletePost.value.time == 0L) return
        progressText.value = "Deleting ..."
        showProgress.value = true

        crudPostJob?.cancel()
        crudPostJob = postUseCases.deletePost(eventDeletePost.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgress.value = true
                is Resource.Success -> {
                    Toast.makeText(application, "Post deleted", Toast.LENGTH_SHORT).show()

                    postsList.removeIf { post -> post.postID == eventDeletePost.value.postID }
                    showProgress.value = false
                }
                is Resource.Error -> {
                    showProgress.value = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    init {
        getSubscribers()
        getAdmins()
        getPostsList()
    }
}
