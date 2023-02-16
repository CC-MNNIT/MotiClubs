@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.data.network.model.AdminDetailResponse
import com.mnnit.moticlubs.data.network.model.ClubDetailModel
import com.mnnit.moticlubs.data.network.model.PostNotificationModel
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel

@Composable
fun ClubScreen(
    appViewModel: AppViewModel,
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    onNavigateToClubDetails: (club: ClubDetailModel) -> Unit,
    onNavigateToImageScreen: (url: String) -> Unit,
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
                BottomSheetContent(viewModel, onNavigateToImageScreen)
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
                bottom = if (appViewModel.user.admin.any { it.clubID == viewModel.clubNavModel.clubId }) 72.dp else 0.dp
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
                    bottomSheetScaffoldState = viewModel.bottomSheetScaffoldState,
                    clubNavModel = viewModel.clubNavModel,
                    postsList = viewModel.postsList,
                    userID = appViewModel.user.id,
                    idx = index,
                    admin = appViewModel.adminMap[viewModel.postsList[index].userID] ?: AdminDetailResponse(),
                    editMode = viewModel.editMode,
                    editPostIdx = viewModel.editPostIdx,
                    postMsg = viewModel.postMsg,
                    imageReplacerMap = viewModel.imageReplacerMap,
                    delPostIdx = viewModel.delPostIdx,
                    showDelPostDialog = viewModel.showDelPostDialog,
                    onNavigateToPost
                )
            }
        }
    }
}
