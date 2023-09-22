package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.util.PublishedMap
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.DragHandle
import com.mnnit.moticlubs.ui.components.channelscreen.ChannelTopBar
import com.mnnit.moticlubs.ui.components.channelscreen.PostCreateUpdateBottomSheet
import com.mnnit.moticlubs.ui.components.channelscreen.PostItem
import com.mnnit.moticlubs.ui.components.isExpanded
import com.mnnit.moticlubs.ui.components.pullrefresh.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.components.pullrefresh.pullRefresh
import com.mnnit.moticlubs.ui.components.pullrefresh.rememberPullRefreshState
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun ChannelScreen(
    onNavigateToPost: (postId: Long) -> Unit,
    onNavigateToClubDetails: (clubId: Long) -> Unit,
    onNavigateToChannelDetails: (channelId: Long) -> Unit,
    onNavigateToImageScreen: (url: String) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChannelScreenViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.loadingPosts.value,
        onRefresh = viewModel::getPostsList,
    )

    MotiClubsTheme {
        SetTransparentSystemBars(setStatusBar = false, setNavBar = false)

        LocalLifecycleOwner.current.lifecycle.addObserver(viewModel)

        Surface(modifier = modifier.systemBarsPadding(), color = colorScheme.background) {
            BottomSheetScaffold(
                modifier = Modifier.imePadding(),
                sheetContent = {
                    PostCreateUpdateBottomSheet(viewModel, onNavigateToImageScreen)
                },
                sheetDragHandle = {
                    DragHandle {
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text(
                                text = if (viewModel.editMode.value) "Update Post" else "Write Post",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()

                                    if (!viewModel.editMode.value && viewModel.eventPostMsg.value.text.isTrimmedNotEmpty()) {
                                        viewModel.showClearDraftDialog.value = true
                                        return@IconButton
                                    }

                                    viewModel.clearEditor()
                                    viewModel.isPreviewMode.value = false
                                    scope.launch {
                                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.partialExpand()
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterVertically),
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "", tint = colorScheme.primary)
                            }
                        }
                    }
                },
                topBar = {
                    Surface(color = colorScheme.surfaceColorAtElevation(2.dp)) {
                        TopBar(
                            viewModel,
                            modifier = Modifier.padding(),
                            onNavigateToClubDetails = onNavigateToClubDetails,
                            onBackPressed = onBackPressed,
                            onNavigateToChannelDetails = onNavigateToChannelDetails,
                        )
                    }
                },
                content = {
                    Box(
                        modifier = Modifier
                            .pullRefresh(state = refreshState, enabled = !viewModel.loadingPosts.value)
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(bottom = if (viewModel.isAdmin) 72.dp else 0.dp),
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        ) {
                            PullDownProgressIndicator(
                                visible = viewModel.loadingPosts.value,
                                refreshState = refreshState,
                            )

                            AnimatedVisibility(
                                visible = viewModel.postsList.value.isEmpty() && !viewModel.loadingPosts.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                            ) {
                                Text(
                                    "No posts yet :/\nPull down to refresh",
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(16.dp),
                                )
                            }

                            Posts(
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f),
                                scrollState = listScrollState,
                                adminMap = viewModel.adminMap,
                                onNavigateToPost = onNavigateToPost,
                            )

                            if (viewModel.showDelPostDialog.value) {
                                DeleteConfirmationDialog(viewModel = viewModel)
                            }
                        }
                    }
                },
                scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = if (viewModel.isAdmin) 72.dp else 0.dp,
                sheetContainerColor = colorScheme.surfaceColorAtElevation(2.dp),
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(viewModel: ChannelScreenViewModel) {
    ConfirmationDialog(
        showDialog = viewModel.showDelPostDialog,
        message = "Are you sure you want to delete this post ?",
        positiveBtnText = "Delete",
        imageVector = Icons.Rounded.Delete,
        onPositive = { viewModel.deletePost() },
    )
}

@Composable
fun TopBar(
    viewModel: ChannelScreenViewModel,
    onNavigateToClubDetails: (clubId: Long) -> Unit,
    onNavigateToChannelDetails: (channelId: Long) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        com.mnnit.moticlubs.ui.components.channelscreen.SearchBar(
            viewModel.searchMode,
            viewModel.searchValue,
            modifier = modifier,
        )
    }
    AnimatedVisibility(visible = !viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        ChannelTopBar(
            viewModel = viewModel,
            modifier = modifier,
            onNavigateToClubDetails = onNavigateToClubDetails,
            onBackPressed = onBackPressed,
            onNavigateToChannelDetails = onNavigateToChannelDetails,
        )
    }
}

@Composable
fun Posts(
    viewModel: ChannelScreenViewModel,
    adminMap: PublishedMap<Long, AdminUser>,
    scrollState: LazyListState,
    onNavigateToPost: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(top = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            items(viewModel.postsList.value.size) { index ->
                if (viewModel.searchMode.value && viewModel.searchValue.value.isTrimmedNotEmpty() &&
                    !viewModel.postsList.value[index].message.toLowerCase(LocaleList.current)
                        .contains(viewModel.searchValue.value.toLowerCase(LocaleList.current))
                ) {
                    return@items
                }
                PostItem(
                    bottomSheetScaffoldState = viewModel.bottomSheetScaffoldState,
                    channelModel = viewModel.channelModel,
                    post = viewModel.postsList.value[index],
                    userId = viewModel.userId,
                    admin = adminMap.value[viewModel.postsList.value[index].userId] ?: AdminUser(),
                    editMode = viewModel.editMode,
                    eventUpdatePost = viewModel.eventUpdatePost,
                    postMsg = viewModel.eventPostMsg,
                    imageReplacerMap = viewModel.eventImageReplacerMap,
                    eventDeletePost = viewModel.eventDeletePost,
                    showDelPostDialog = viewModel.showDelPostDialog,
                    onNavigateToPost,
                )

                LaunchedEffect(index) {
                    if (index == viewModel.postsList.value.size - 1) {
                        viewModel.getPostsList(refresh = false)
                    }
                }
            }
        }
    }
}
