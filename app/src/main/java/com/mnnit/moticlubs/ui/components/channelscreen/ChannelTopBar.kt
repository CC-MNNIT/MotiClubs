package com.mnnit.moticlubs.ui.components.channelscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelScreenViewModel

@Composable
fun ChannelTopBar(
    viewModel: ChannelScreenViewModel,
    onNavigateToClubDetails: (clubId: Long) -> Unit,
    onNavigateToChannelDetails: (channelId: Long) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onNavigateToChannelDetails(viewModel.channelId) },
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentSize()
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(42.dp),
                    onClick = onBackPressed
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                }

                Spacer(modifier = Modifier.padding(4.dp))

                ProfilePicture(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    userModel = User().copy(avatar = viewModel.clubModel.avatar),
                    size = 42.dp,
                    onClick = { onNavigateToClubDetails(viewModel.clubId) }
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                // Channel name
                Text(
                    modifier = Modifier,
                    text = viewModel.channelModel.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Number of members
                Text(
                    text = "${viewModel.clubModel.name} - ${
                        if (viewModel.memberCount.intValue == -1) "General" else {
                            "${viewModel.memberCount.intValue} members"
                        }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .wrapContentSize()
            ) {
                // Search icon
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(42.dp),
                    onClick = { viewModel.searchMode.value = true }
                ) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "")
                }
            }
        }
    }
}
