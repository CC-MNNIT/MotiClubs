@file:OptIn(ExperimentalLayoutApi::class)

package com.mnnit.moticlubs.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.ChannelModel
import com.mnnit.moticlubs.network.model.ClubModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {

    val clubsList = mutableStateListOf<ClubModel>()
    var isFetching by mutableStateOf(false)

    private fun fetchClubsList() {
        isFetching = true
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.getClubs(application) }
            if (response is Success) {
                clubsList.clear()
                clubsList.addAll(response.obj)
            }
            isFetching = false
        }
    }

    init {
        fetchClubsList()
    }
}

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onNavigatePostItemClick: (channel: ChannelModel, club: ClubModel) -> Unit,
    onNavigateContactUs: () -> Unit,
    onNavigateProfile: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()
        Scaffold(
            modifier = Modifier,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(it)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    ProfilePicture(
                        modifier = Modifier.align(Alignment.End),
                        url = appViewModel.user.avatar,
                        onClick = { onNavigateProfile() })

                    Text(text = "MNNIT Clubs", fontSize = 28.sp)

                    AnimatedVisibility(visible = viewModel.isFetching, modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    AnimatedVisibility(
                        visible = viewModel.clubsList.isEmpty() && !viewModel.isFetching,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Error loading clubs :/",
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    ClubList(clubsList = viewModel.clubsList, onNavigatePostItemClick = onNavigatePostItemClick)
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Contact Us", fontSize = 15.sp, textAlign = TextAlign.Center) },
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
    clubsList: SnapshotStateList<ClubModel>,
    onNavigatePostItemClick: (channel: ChannelModel, club: ClubModel) -> Unit
) {
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxHeight(),
        contentPadding = PaddingValues()
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
                shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    ProfilePicture(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        url = clubsList[idx].avatar
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
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
                    ChannelList(list = clubsList[idx].channels, clubsList[idx], onNavigatePostItemClick)
                }
            }
        }
    }
}

@Composable
fun ChannelList(
    list: List<ChannelModel>,
    clubModel: ClubModel,
    onNavigatePostItemClick: (channel: ChannelModel, club: ClubModel) -> Unit
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
                            .fillMaxWidth(0.9f)
                    )

                    AnimatedVisibility(
                        visible = context.getUnreadPost(model.channelID).isNotEmpty(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        BadgedBox(badge = {
                            Badge { Text(text = "${context.getUnreadPost(model.channelID).size}") }
                        }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                    }
                }
            }
        }
    }
}
