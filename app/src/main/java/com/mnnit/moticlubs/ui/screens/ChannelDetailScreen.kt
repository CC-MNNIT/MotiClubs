package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.ui.components.CollapsibleTopAppBar
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.components.homescreen.UpdateChannelDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelDetailScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChannelDetailScreen(
    onDeleteChannel: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ChannelDetailScreenViewModel = hiltViewModel(),
) {
    val colorScheme = getColorScheme()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetching,
        onRefresh = viewModel::refreshAll
    )
    MotiClubsTheme(colorScheme = getColorScheme()) {
        if (scrollBehavior.state.collapsedFraction > 0.6f) {
            SetNavBarsTheme(2.dp, false)
        } else {
            SetNavBarsTheme()
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    CollapsibleTopAppBar(
                        modifier = Modifier.wrapContentHeight(),
                        bigTitle = { BigTitle(viewModel) },
                        smallTitle = {
                            Text(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState()),
                                text = viewModel.channelModel.name,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                modifier = Modifier.size(42.dp),
                                onClick = onBackPressed
                            ) {
                                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                            }
                        },
                        actions = { Actions(viewModel) },
                        scrollBehavior = scrollBehavior,
                    )
                },
                content = {
                    if (viewModel.isUpdating) {
                        ProgressDialog(viewModel.progressMsg)
                    }

                    if (viewModel.showPrivateConfirmationDialog.value) {
                        ConfirmationDialog(
                            showDialog = viewModel.showPrivateConfirmationDialog,
                            message = "${
                                if (viewModel.updateChannelPrivate == 0) {
                                    "Making channel public will allow all the users to access the channel."
                                } else {
                                    "Making channel private will restrict access to only admins of club and members of the channel."
                                }
                            }\n\nAre you sure you want to continue ?",
                            positiveBtnText = "Continue",
                            onPositive = { viewModel.updateChannel() },
                            onNegative = { viewModel.resetUpdate() }
                        )
                    }

                    if (viewModel.showUpdateChannelDialog.value) {
                        UpdateChannelDialog(viewModel, onUpdate = { viewModel.updateChannel() }) {
                            viewModel.deleteChannel(onDeleteChannel)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .padding(it)
                            .pullRefresh(
                                state = refreshState,
                                enabled = !viewModel.isFetching
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PullDownProgressIndicator(
                            modifier = Modifier.background(colorScheme.surfaceColorAtElevation(2.dp)),
                            visible = viewModel.isFetching,
                            refreshState = refreshState
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(
                            modifier = Modifier,
                            text = "${
                                if (viewModel.channelModel.private == 1) {
                                    viewModel.memberList.size
                                } else "All"
                            } member${if (viewModel.memberList.size > 1) "s" else ""}",
                            fontSize = 14.sp,
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        MemberList(viewModel)
                    }
                }
            )
        }
    }
}

@Composable
private fun BigTitle(viewModel: ChannelDetailScreenViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            text = viewModel.channelModel.name,
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            text = viewModel.clubModel.name,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Private",
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 14.sp
            )
            Switch(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                enabled = viewModel.channelModel.name != "General" && viewModel.isAdmin,
                checked = viewModel.updateChannelPrivate == 1,
                onCheckedChange = {
                    if (viewModel.isAdmin) {
                        viewModel.updateChannelPrivate = viewModel.updateChannelPrivate.xor(1)
                        viewModel.showPrivateConfirmationDialog.value = true
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.Actions(viewModel: ChannelDetailScreenViewModel) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = viewModel.channelModel.name != "General" && viewModel.isAdmin,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        IconButton(
            modifier = Modifier.size(42.dp),
            onClick = {
                if (viewModel.isAdmin) {
                    Toast.makeText(context, "Will implement", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Icon(imageVector = Icons.Rounded.GroupAdd, contentDescription = "")
        }
    }
    Spacer(modifier = Modifier.padding(4.dp))
    AnimatedVisibility(
        visible = viewModel.channelModel.name != "General" && viewModel.isAdmin,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        IconButton(
            modifier = Modifier.size(42.dp),
            onClick = {
                if (viewModel.isAdmin) {
                    viewModel.updateChannelName = viewModel.channelModel.name
                    viewModel.showUpdateChannelDialog.value = true
                }
            }
        ) {
            Icon(imageVector = Icons.Rounded.Edit, contentDescription = "")
        }
    }
}

@Composable
private fun MemberList(
    viewModel: ChannelDetailScreenViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()

    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = 56.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            items(viewModel.memberList.size) {
                val member = viewModel.memberList[it]
                if (!viewModel.memberInfo.containsKey(member.userId)) {
                    viewModel.getUser(member.userId)
                }

                MemberItem(member, viewModel)
            }
        }
    }
}

@Composable
private fun MemberItem(
    member: Member,
    viewModel: ChannelDetailScreenViewModel,
) {
    val colorScheme = getColorScheme()

    Card(
        modifier = Modifier
            .safeContentPadding()
            .padding(top = 8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.CenterVertically),
                url = viewModel.memberInfo[member.userId]?.avatar ?: "",
                size = 48.dp
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = viewModel.memberInfo[member.userId]?.name ?: "...",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.adminList.any { admin -> member.userId == admin.userId }) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(16.dp)
                        .align(Alignment.CenterVertically),
                    colors = CardDefaults.cardColors(colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = "Admin",
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
