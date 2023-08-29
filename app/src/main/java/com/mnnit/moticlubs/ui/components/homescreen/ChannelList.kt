package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.util.getUnreadPost
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun ChannelList(
    list: List<Channel>,
    viewModel: HomeScreenViewModel,
    clubModel: Club,
    onNavigateChannelClick: (channelId: Long, clubId: Long) -> Unit
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
                onClick = { onNavigateChannelClick(model.channelId, clubModel.clubId) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(42.dp)
                            .wrapContentSize(),
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterVertically),
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "",
                            tint = if (model.private == 1) {
                                LocalContentColor.current
                            } else colorScheme.surfaceColorAtElevation(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        model.name,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.padding(12.dp))

                    AnimatedVisibility(
                        visible = context.getUnreadPost(model.channelId).isNotEmpty(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Badge(
                            Modifier
                                .align(Alignment.CenterVertically)
                                .wrapContentSize()
                        ) { Text(text = "${context.getUnreadPost(model.channelId).size}") }
                    }

                    Spacer(modifier = Modifier.padding(12.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = viewModel.adminList.any {
                it.userId == viewModel.userModel.userId && it.clubId == clubModel.clubId
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                onClick = {
                    viewModel.eventChannel = Channel(-1L, clubModel.clubId, "", 0)
                    viewModel.inputChannelName = ""
                    viewModel.inputChannelPrivate = 0
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
