package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.util.clubHasUnreadPost
import com.mnnit.moticlubs.domain.util.getExpandedChannel
import com.mnnit.moticlubs.domain.util.setExpandedChannel
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun ClubList(
    viewModel: HomeScreenViewModel,
    clubsList: SnapshotStateList<Club>,
    channelMap: MutableMap<Long, SnapshotStateList<Channel>>,
    onNavigateChannelClick: (channel: Channel, club: Club) -> Unit
) {
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(bottom = 72.dp, top = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        items(clubsList.size) { idx ->
            var channelVisibility by remember { mutableStateOf(context.getExpandedChannel(clubsList[idx].clubId)) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    channelVisibility = !channelVisibility
                    context.setExpandedChannel(clubsList[idx].clubId, channelVisibility)
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
                        Text(
                            text = clubsList[idx].name,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = clubsList[idx].summary,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    AnimatedVisibility(
                        visible = context.clubHasUnreadPost(
                            channelMap.getOrDefault(clubsList[idx].clubId, mutableListOf())
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        BadgedBox(badge = {
                            Badge { Text(text = " ") }
                        }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                    }
                }

                AnimatedVisibility(visible = channelVisibility) {
                    ChannelList(
                        list = channelMap.getOrDefault(clubsList[idx].clubId, mutableListOf()),
                        viewModel,
                        clubsList[idx],
                        onNavigateChannelClick
                    )
                }
            }
        }
    }
}
