@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.components.homescreen.ClubList
import com.mnnit.moticlubs.ui.components.homescreen.InputChannelDialog
import com.mnnit.moticlubs.ui.components.homescreen.UpdateChannelDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onNavigateChannelClick: (channel: Channel, club: Club) -> Unit,
    onNavigateContactUs: () -> Unit,
    onNavigateProfile: (viewModel: HomeScreenViewModel) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val colorScheme = getColorScheme()
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetchingAdmins || viewModel.isFetchingChannels || viewModel.isFetchingClubs,
        onRefresh = viewModel::refreshAll
    )
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = "MNNIT Clubs", fontSize = 28.sp) },
                    actions = {
                        ProfilePicture(
                            modifier = Modifier.padding(end = 16.dp),
                            url = appViewModel.user.avatar,
                            onClick = { onNavigateProfile(viewModel) })
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = colorScheme.background)
                )
            },
            content = {
                if (viewModel.showProgressDialog) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showAddChannelDialog) {
                    InputChannelDialog(viewModel = viewModel) { viewModel.addChannel() }
                }

                if (viewModel.showUpdateChannelDialog) {
                    UpdateChannelDialog(
                        viewModel = viewModel,
                        onUpdate = { viewModel.updateChannel() },
                        onDelete = { viewModel.deleteChannel() }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .pullRefresh(
                            state = refreshState,
                            enabled = !viewModel.isFetchingAdmins
                                    && !viewModel.isFetchingChannels
                                    && !viewModel.isFetchingClubs
                        )
                ) {
                    PullDownProgressIndicator(
                        visible = viewModel.isFetchingAdmins
                                || viewModel.isFetchingChannels
                                || viewModel.isFetchingClubs,
                        refreshState = refreshState
                    )

                    AnimatedVisibility(
                        visible = viewModel.clubsList.isEmpty() && !viewModel.isFetchingAdmins
                                && !viewModel.isFetchingChannels
                                && !viewModel.isFetchingClubs,
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(
                                state = refreshState, enabled = !viewModel.isFetchingAdmins
                                        && !viewModel.isFetchingChannels
                                        && !viewModel.isFetchingClubs
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "Error loading clubs :/\nPull down to refresh",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 16.dp)
                        )
                    }
                    ClubList(
                        viewModel,
                        clubsList = viewModel.clubsList,
                        channelMap = viewModel.channelMap,
                        onNavigateChannelClick = onNavigateChannelClick
                    )
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = "About Us", fontSize = 15.sp, textAlign = TextAlign.Center) },
                    icon = { Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = "") },
                    onClick = { onNavigateContactUs() },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding()
                )
            }
        )
    }
}
