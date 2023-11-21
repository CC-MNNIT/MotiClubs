package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.PublishedList
import com.mnnit.moticlubs.domain.util.PublishedMap
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.setExpandedChannel
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun ClubList(
    viewModel: HomeScreenViewModel,
    listState: LazyListState,
    clubsList: PublishedList<Club>,
    channelMap: PublishedMap<Long, PublishedList<Channel>>,
    onNavigateChannelClick: (channelId: Long, clubId: Long) -> Unit,
    onNavigateToClubDetails: (clubId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val emptyList = remember { publishedStateListOf<Channel>() }

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(bottom = 72.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        state = listState,
    ) {
        items(clubsList.value.size) { idx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    viewModel.clubsInfo.value[idx] = viewModel.clubsInfo.value[idx].copy(
                        !viewModel.clubsInfo.value[idx].first,
                    )
                    context.setExpandedChannel(clubsList.value[idx].clubId, viewModel.clubsInfo.value[idx].first)
                },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(if (viewModel.clubsInfo.value[idx].first) 8.dp else 0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    ProfilePicture(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        userModel = User().copy(avatar = clubsList.value[idx].avatar),
                        onClick = { onNavigateToClubDetails(viewModel.clubsList.value[idx].clubId) },
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                    ) {
                        Text(
                            text = clubsList.value[idx].name,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = clubsList.value[idx].summary,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    AnimatedVisibility(
                        visible = viewModel.clubsInfo.value[idx].second,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) {
                        BadgedBox(
                            badge = {
                                Badge { Text(text = " ") }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically),
                        ) {}
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                AnimatedVisibility(visible = viewModel.clubsInfo.value[idx].first) {
                    ChannelList(
                        list = channelMap.value.getOrDefault(clubsList.value[idx].clubId, emptyList),
                        viewModel = viewModel,
                        clubModel = clubsList.value[idx],
                        onNavigateChannelClick = onNavigateChannelClick,
                    )
                }
            }
        }
    }
}
