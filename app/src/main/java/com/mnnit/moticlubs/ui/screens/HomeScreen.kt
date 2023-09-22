package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.homescreen.ClubList
import com.mnnit.moticlubs.ui.components.homescreen.InputChannelDialog
import com.mnnit.moticlubs.ui.components.pullrefresh.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.components.pullrefresh.pullRefresh
import com.mnnit.moticlubs.ui.components.pullrefresh.rememberPullRefreshState
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    onNavigateChannelClick: (channelId: Long, clubId: Long) -> Unit,
    onNavigateToClubDetails: (clubId: Long) -> Unit,
    onNavigateContactUs: () -> Unit,
    onNavigateProfile: (viewModel: HomeScreenViewModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetchingAdmins || viewModel.isFetchingHome,
        onRefresh = viewModel::refreshAll,
    )
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    MotiClubsTheme {
        SetTransparentSystemBars(setStatusBar = scrollBehavior.state.collapsedFraction <= 0.6f)

        LocalLifecycleOwner.current.lifecycle.addObserver(viewModel)

        Scaffold(
            modifier = modifier
                .statusBarsPadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = { Text(text = "MNNIT Clubs", fontSize = 28.sp) },
                    actions = {
                        ProfilePicture(
                            modifier = Modifier.padding(end = 16.dp),
                            userModel = viewModel.userModel,
                            onClick = { onNavigateProfile(viewModel) },
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        scrolledContainerColor = colorScheme.surfaceColorAtElevation(2.dp),
                    ),
                )
            },
            content = {
                if (viewModel.showProgressDialog) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showAddChannelDialog) {
                    InputChannelDialog(viewModel = viewModel, onClick = { viewModel.addChannel() })
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingValues(top = it.calculateTopPadding()))
                        .pullRefresh(
                            state = refreshState,
                            enabled = !viewModel.isFetchingAdmins && !viewModel.isFetchingHome,
                        ),
                ) {
                    PullDownProgressIndicator(
                        visible = viewModel.isFetchingAdmins || viewModel.isFetchingHome,
                        refreshState = refreshState,
                    )

                    AnimatedVisibility(
                        visible = viewModel.clubsList.value.isEmpty() && !viewModel.isFetchingAdmins &&
                            !viewModel.isFetchingHome,
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(
                                state = refreshState,
                                enabled = !viewModel.isFetchingAdmins && !viewModel.isFetchingHome,
                            )
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Text(
                            "Error loading clubs :/\nPull down to refresh",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp),
                        )
                    }
                    ClubList(
                        viewModel,
                        clubsList = viewModel.clubsList,
                        channelMap = viewModel.channelMap,
                        onNavigateChannelClick = onNavigateChannelClick,
                        onNavigateToClubDetails = onNavigateToClubDetails,
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onNavigateContactUs() },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(),
                ) {
                    Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = "")
                }
            },
        )
    }
}
