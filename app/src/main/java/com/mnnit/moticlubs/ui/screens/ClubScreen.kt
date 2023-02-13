@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.app.Application
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.network.*
import com.mnnit.moticlubs.network.model.*
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
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
    val postsList = mutableStateListOf<PostModel>()
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
            val response = withContext(Dispatchers.IO) {
                repository.getPostsFromClubChannel(application, clubID = clubID, channelID = channelID)
            }
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
            val response = withContext(Dispatchers.IO) { repository.getSubscribersCount(application, clubID) }
            if (response is Success) {
                subscriberCount.value = response.obj.count
                Log.d("TAG", "fetchSubscriberCount: ${response.obj.count}")
            }
        }
    }

    fun subscribeToClub(clubID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.subscribeClub(application, clubID) }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun unsubscribeToClub(clubID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.unsubscribeClub(application, clubID) }
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
            val response = withContext(Dispatchers.IO) {
                repository.sendPost(
                    application,
                    PushPostModel(clubID, channelID, message, true)
                )
            }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun updatePost(postID: Int, message: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.updatePost(application, postID, message) }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun deletePost(postID: Int, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.deletePost(application, postID) }
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

@Composable
fun ClubScreen(
    appViewModel: AppViewModel,
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    onNavigateToClubDetails: (club: ClubDetailModel) -> Unit,
    viewModel: ClubScreenViewModel = hiltViewModel()
) {
    viewModel.subscribed.value = appViewModel.user.subscribed.any { it.clubID == viewModel.clubNavModel.clubId }

    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.loadingPosts.value,
        onRefresh = viewModel::fetchPostsList
    )

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(elevation = 2.dp, appViewModel.user.admin.any { it.clubID == viewModel.clubNavModel.clubId })

        Surface(modifier = Modifier.imePadding(), color = colorScheme.background) {
            BottomSheetScaffold(modifier = Modifier.imePadding(), sheetContent = {
                BottomSheetContent(viewModel)
            }, topBar = {
                Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                    TopBar(
                        viewModel,
                        appViewModel,
                        modifier = Modifier.padding(),
                        onNavigateToClubDetails = onNavigateToClubDetails
                    )
                }
            }, content = {
                Box(
                    modifier = Modifier
                        .pullRefresh(state = refreshState, enabled = !viewModel.loadingPosts.value)
                        .fillMaxSize()
                        .background(colorScheme.background)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        AnimatedVisibility(
                            visible = viewModel.loadingPosts.value || refreshState.progress.dp.value > 0.5f,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                strokeCap = StrokeCap.Round
                            )
                        }

                        AnimatedVisibility(
                            visible = viewModel.postsList.isEmpty() && !viewModel.loadingPosts.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "No posts yet :/\nPull down to refresh",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                            )
                        }

                        Messages(
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f),
                            scrollState = listScrollState,
                            appViewModel = appViewModel,
                            onNavigateToPost = onNavigateToPost
                        )

                        if (viewModel.showDelPostDialog.value) {
                            DeleteConfirmationDialog(viewModel = viewModel)
                        }
                    }
                }
            }, scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = if (appViewModel.user.admin.any { it.clubID == viewModel.clubNavModel.clubId }) {
                    72.dp
                } else {
                    0.dp
                }, sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp)
            )
        }
    }
}

@Composable
fun PostConfirmationDialog(viewModel: ClubScreenViewModel, update: Boolean, onPost: () -> Unit) {
    ConfirmationDialog(
        showDialog = if (update) viewModel.showEditDialog else viewModel.showDialog,
        message = "${if (update) "Update post" else "Post"} message in ${viewModel.clubNavModel.name} ?",
        positiveBtnText = if (update) "Update" else "Post",
        imageVector = Icons.Outlined.Article,
        onPositive = {
            viewModel.progressText.value = if (update) "Updating ..." else "Posting ..."
            viewModel.showProgress.value = true
            onPost()
        }
    )
}

@Composable
fun DeleteConfirmationDialog(viewModel: ClubScreenViewModel) {
    val context = LocalContext.current
    ConfirmationDialog(
        showDialog = viewModel.showDelPostDialog,
        message = "Are you sure you want to delete this post ?",
        positiveBtnText = "Delete",
        imageVector = Icons.Rounded.Delete,
        onPositive = {
            viewModel.progressText.value = "Deleting ..."
            viewModel.showProgress.value = true
            if (viewModel.delPostIdx.value < 0) return@ConfirmationDialog
            viewModel.deletePost(viewModel.postsList[viewModel.delPostIdx.value].postID, {
                viewModel.showProgress.value = false
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                viewModel.fetchPostsList()
            }, {
                viewModel.showProgress.value = false
                Toast.makeText(context, "$it: Error deleting post", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    )
}

@Composable
fun InputLinkDialog(viewModel: ClubScreenViewModel) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showLinkDialog.value = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Link Input",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputLinkName.value,
                    onValueChange = { viewModel.inputLinkName.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputLink.value,
                    onValueChange = { viewModel.inputLink.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        val post = viewModel.postMsg.value.text
                        val selection = viewModel.postMsg.value.selection
                        val link = "\n[${viewModel.inputLinkName.value}](${viewModel.inputLink.value})\n"
                        viewModel.postMsg.value = TextFieldValue(
                            post.replaceRange(selection.start, selection.end, link),
                            selection = TextRange(selection.end + link.length, selection.end + link.length)
                        )
                        viewModel.showLinkDialog.value = false
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.inputLink.value.matches(Patterns.WEB_URL.toRegex())
                ) {
                    Text(text = "Add Link", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun TopBar(
    viewModel: ClubScreenViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: (clubModel: ClubDetailModel) -> Unit
) {
    AnimatedVisibility(visible = viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        SearchBar(viewModel.searchMode, viewModel.searchValue, modifier = modifier)
    }
    AnimatedVisibility(visible = !viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        ChannelNameBar(
            viewModel = viewModel,
            appViewModel = appViewModel,
            modifier = modifier,
            onNavigateToClubDetails = onNavigateToClubDetails
        )
    }
}

@Composable
fun Messages(
    viewModel: ClubScreenViewModel,
    scrollState: LazyListState,
    appViewModel: AppViewModel,
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = if (appViewModel.user.admin.any { it.clubID == viewModel.clubNavModel.clubId }) {
                    72.dp
                } else {
                    0.dp
                }
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            items(viewModel.postsList.size) { index ->
                if (viewModel.searchMode.value && viewModel.searchValue.value.isNotEmpty() &&
                    !viewModel.postsList[index].message.contains(viewModel.searchValue.value)
                ) {
                    return@items
                }
                PostItem(
                    viewModel,
                    appViewModel,
                    index,
                    admin = appViewModel.adminMap[viewModel.postsList[index].userID] ?: AdminDetailResponse(),
                    onNavigateToPost
                )
            }
        }
    }
}
