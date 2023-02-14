@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.clubHasUnreadPost
import com.mnnit.moticlubs.getExpandedChannel
import com.mnnit.moticlubs.getUnreadPost
import com.mnnit.moticlubs.data.network.model.ChannelDto
import com.mnnit.moticlubs.data.network.model.ClubModel
import com.mnnit.moticlubs.setExpandedChannel
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onNavigatePostItemClick: (channel: ChannelDto, club: ClubModel) -> Unit,
    onNavigateContactUs: () -> Unit,
    onNavigateProfile: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val colorScheme = getColorScheme()
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetching,
        onRefresh = viewModel::fetchClubsList
    )

    val context = LocalContext.current
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()
        Scaffold(
            modifier = Modifier,
            content = {
                if (viewModel.showProgressDialog) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showAddChannelDialog) {
                    InputChannelDialog(viewModel = viewModel) {
                        viewModel.showAddChannelDialog = false
                        viewModel.progressMsg = "Adding"
                        viewModel.showProgressDialog = true

                        viewModel.addChannel({
                            viewModel.showProgressDialog = false

                            viewModel.fetchClubsList()
                            Toast.makeText(context, "Channel Added", Toast.LENGTH_SHORT).show()
                        }) { code ->
                            viewModel.showProgressDialog = false
                            Toast.makeText(context, "$code: Error adding channel", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                if (viewModel.showUpdateChannelDialog) {
                    UpdateChannelDialog(viewModel = viewModel, onUpdate = {
                        viewModel.showUpdateChannelDialog = false
                        viewModel.progressMsg = "Updating"
                        viewModel.showProgressDialog = true

                        viewModel.updateChannel({
                            viewModel.showProgressDialog = false

                            viewModel.fetchClubsList()
                            Toast.makeText(context, "Channel Updated", Toast.LENGTH_SHORT).show()
                        }) { code ->
                            viewModel.showProgressDialog = false
                            Toast.makeText(context, "$code: Error updating channel", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        viewModel.showUpdateChannelDialog = false
                        viewModel.progressMsg = "Deleting"
                        viewModel.showProgressDialog = true

                        viewModel.deleteChannel({
                            viewModel.showProgressDialog = false

                            viewModel.fetchClubsList()
                            Toast.makeText(context, "Channel Deleted", Toast.LENGTH_SHORT).show()
                        }) { code ->
                            viewModel.showProgressDialog = false
                            Toast.makeText(context, "$code: Error deleting channel", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight()
                        .consumeWindowInsets(it)
                        .pullRefresh(state = refreshState, enabled = !viewModel.isFetching)
                        .padding(top = 16.dp)
                ) {
                    ProfilePicture(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 16.dp),
                        url = appViewModel.user.avatar,
                        onClick = { onNavigateProfile() })

                    Text(modifier = Modifier.padding(start = 16.dp), text = "MNNIT Clubs", fontSize = 28.sp)

                    AnimatedVisibility(
                        visible = viewModel.isFetching || refreshState.progress.dp.value > 0.5f,
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
                        visible = viewModel.clubsList.isEmpty() && !viewModel.isFetching,
                        modifier = Modifier.fillMaxWidth()
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
                        appViewModel,
                        clubsList = viewModel.clubsList,
                        onNavigatePostItemClick = onNavigatePostItemClick
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

@Composable
fun ClubList(
    viewModel: HomeScreenViewModel,
    appViewModel: AppViewModel,
    clubsList: SnapshotStateList<ClubModel>,
    onNavigatePostItemClick: (channel: ChannelDto, club: ClubModel) -> Unit
) {
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp, start = 16.dp, end = 16.dp)
    ) {
        items(clubsList.size) { idx ->
            var channelVisibility by remember { mutableStateOf(context.getExpandedChannel(clubsList[idx].id)) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    channelVisibility = !channelVisibility
                    context.setExpandedChannel(clubsList[idx].id, channelVisibility)
                },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(if (channelVisibility) 8.dp else 0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    ProfilePicture(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        url = clubsList[idx].avatar
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth(0.9f)
                    ) {
                        Text(text = clubsList[idx].name, fontSize = 16.sp)
                        Text(
                            text = clubsList[idx].summary,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            softWrap = true,
                            maxLines = 2
                        )
                    }

                    AnimatedVisibility(
                        visible = context.clubHasUnreadPost(clubsList[idx]),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        BadgedBox(badge = {
                            Badge { Text(text = " ") }
                        }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                    }
                }

                AnimatedVisibility(visible = channelVisibility) {
                    ChannelList(
                        list = clubsList[idx].channels,
                        appViewModel,
                        viewModel,
                        clubsList[idx],
                        onNavigatePostItemClick
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelList(
    list: List<ChannelDto>,
    appViewModel: AppViewModel,
    viewModel: HomeScreenViewModel,
    clubModel: ClubModel,
    onNavigatePostItemClick: (channel: ChannelDto, club: ClubModel) -> Unit
) {
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topEnd = 0.dp, topStart = 0.dp, bottomEnd = 24.dp, bottomStart = 24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
    ) {
        list.forEach { model ->
            Card(
                onClick = { onNavigatePostItemClick(model, clubModel) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        model.name,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 16.dp)
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth(if (appViewModel.user.admin.any { it.clubID == clubModel.id }) 0.8f else 0.9f)
                    )

                    AnimatedVisibility(
                        visible = context.getUnreadPost(model.channelID).isNotEmpty(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        BadgedBox(
                            badge = {
                                Badge { Text(text = "${context.getUnreadPost(model.channelID).size}") }
                            }, modifier = Modifier.align(Alignment.CenterVertically)
                        ) {}
                    }

                    AnimatedVisibility(
                        visible = appViewModel.user.admin.any { it.clubID == clubModel.id } && model.name != "General",
                        modifier = Modifier
                    ) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(top = 1.dp, start = 16.dp)
                                .height(36.dp),
                            onClick = {
                                viewModel.channelID = model.channelID
                                viewModel.updateChannel = model.name
                                viewModel.showUpdateChannelDialog = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.CenterVertically),
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = appViewModel.user.admin.any { it.clubID == clubModel.id },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                onClick = {
                    viewModel.clubID = clubModel.id
                    viewModel.showAddChannelDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Add Channel",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                            .align(Alignment.CenterVertically)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InputChannelDialog(viewModel: HomeScreenViewModel, onClick: () -> Unit) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showAddChannelDialog = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "New Channel",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = viewModel.inputChannel,
                    onValueChange = { viewModel.inputChannel = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Channel Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = { onClick() },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.inputChannel.isNotEmpty()
                ) {
                    Text(text = "Add Channel", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun UpdateChannelDialog(viewModel: HomeScreenViewModel, onUpdate: () -> Unit, onDelete: () -> Unit) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showUpdateChannelDialog = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Update Channel",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = viewModel.updateChannel,
                    onValueChange = { viewModel.updateChannel = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Channel Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onDelete() },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(colorScheme.error)
                    ) {
                        Text(text = "Delete", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onUpdate() },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        enabled = viewModel.updateChannel.isNotEmpty()
                    ) {
                        Text(text = "Save", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
