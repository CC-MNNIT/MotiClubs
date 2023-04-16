@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel

@Composable
fun ClubScreen(
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    onNavigateToClubDetails: (club: Club, user: User) -> Unit,
    onNavigateToImageScreen: (url: String) -> Unit,
    viewModel: ClubScreenViewModel = hiltViewModel()
) {
    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.loadingPosts.value,
        onRefresh = viewModel::getPostsList
    )

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(elevation = 2.dp, true)

        Surface(modifier = Modifier.imePadding(), color = colorScheme.background) {
            BottomSheetScaffold(
                modifier = Modifier.imePadding(),
                sheetContent = {
                    BottomSheetContent(viewModel, onNavigateToImageScreen)
                },
                topBar = {
                    Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                        TopBar(
                            viewModel,
                            modifier = Modifier.padding(),
                            onNavigateToClubDetails = onNavigateToClubDetails
                        )
                    }
                },
                content = {
                    Box(
                        modifier = Modifier
                            .pullRefresh(state = refreshState, enabled = !viewModel.loadingPosts.value)
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(bottom = if (viewModel.isAdmin) 72.dp else 0.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                        ) {
                            PullDownProgressIndicator(
                                visible = viewModel.loadingPosts.value,
                                refreshState = refreshState
                            )

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
                                adminMap = viewModel.adminMap,
                                onNavigateToPost = onNavigateToPost
                            )

                            AnimatedVisibility(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .weight(0.03f),
                                visible = viewModel.paging,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                androidx.compose.material3.LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    strokeCap = StrokeCap.Round
                                )
                            }

                            if (viewModel.showDelPostDialog.value) {
                                DeleteConfirmationDialog(viewModel = viewModel)
                            }
                        }
                    }
                },
                scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = if (viewModel.isAdmin) 72.dp else 0.dp,
                sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp)
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(viewModel: ClubScreenViewModel) {
    ConfirmationDialog(
        showDialog = viewModel.showDelPostDialog,
        message = "Are you sure you want to delete this post ?",
        positiveBtnText = "Delete",
        imageVector = Icons.Rounded.Delete,
        onPositive = { viewModel.deletePost() }
    )
}

@Composable
fun TopBar(
    viewModel: ClubScreenViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: (clubModel: Club, user: User) -> Unit
) {
    AnimatedVisibility(visible = viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        SearchBar(viewModel.searchMode, viewModel.searchValue, modifier = modifier)
    }
    AnimatedVisibility(visible = !viewModel.searchMode.value, enter = fadeIn(), exit = fadeOut()) {
        ChannelNameBar(
            viewModel = viewModel,
            modifier = modifier,
            onNavigateToClubDetails = onNavigateToClubDetails
        )
    }
}

@Composable
fun Messages(
    viewModel: ClubScreenViewModel,
    adminMap: MutableMap<Int, User>,
    scrollState: LazyListState,
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(top = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(viewModel.postsList.size) { index ->
                if (viewModel.searchMode.value && viewModel.searchValue.value.isNotEmpty() &&
                    !viewModel.postsList[index].message.toLowerCase(LocaleList.current)
                        .contains(viewModel.searchValue.value.toLowerCase(LocaleList.current))
                ) {
                    return@items
                }
                PostItem(
                    bottomSheetScaffoldState = viewModel.bottomSheetScaffoldState,
                    clubModel = viewModel.clubModel,
                    channelModel = viewModel.channelModel,
                    postsList = viewModel.postsList,
                    userID = viewModel.userModel.userID,
                    idx = index,
                    admin = adminMap[viewModel.postsList[index].userID] ?: User(),
                    editMode = viewModel.editMode,
                    eventUpdatePost = viewModel.eventUpdatePost,
                    postMsg = viewModel.eventPostMsg,
                    imageReplacerMap = viewModel.eventImageReplacerMap,
                    eventDeletePost = viewModel.eventDeletePost,
                    showDelPostDialog = viewModel.showDelPostDialog,
                    onNavigateToPost
                )
            }
            item {
                LaunchedEffect(scrollState.canScrollForward) {
                    if (!scrollState.canScrollForward && !viewModel.loadingPosts.value && !viewModel.pageEnded) {
                        viewModel.getPostsList(refresh = false)
                    }
                }
            }
        }
    }
}
