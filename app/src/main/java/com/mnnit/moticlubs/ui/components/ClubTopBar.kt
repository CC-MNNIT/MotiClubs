package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.data.network.model.ClubDetailModel
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: (clubModel: ClubDetailModel) -> Unit
) {
    if (viewModel.showSubsDialog.value) {
        val subscribe = !viewModel.subscribed.value
        ConfirmationDialog(
            showDialog = viewModel.showSubsDialog,
            message = "Are you sure you want to ${if (subscribe) "subscribe" else "unsubscribe"} ?",
            positiveBtnText = if (subscribe) "Subscribe" else "Unsubscribe",
            imageVector = if (subscribe) Icons.Rounded.NotificationsActive else Icons.Outlined.NotificationsOff,
            onPositive = {
                viewModel.progressText.value = if (subscribe) "Subscribing ..." else "Unsubscribing ..."
                viewModel.subscribeToClub(appViewModel, subscribe)
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(true, onClick = {
                onNavigateToClubDetails(
                    ClubDetailModel(
                        viewModel.clubNavModel.clubId,
                        viewModel.clubNavModel.name,
                        viewModel.clubNavModel.description,
                        viewModel.clubNavModel.avatar,
                        viewModel.clubNavModel.summary,
                        listOf(), viewModel.subscriberCount.value
                    )
                )
            }),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ProfilePicture(
            modifier = Modifier.align(Alignment.CenterVertically),
            url = viewModel.clubNavModel.avatar,
            size = 42.dp
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Channel name
            Text(
                text = "${viewModel.clubNavModel.name} - ${viewModel.clubNavModel.channel.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )

            // Number of members
            Text(
                text = "${viewModel.subscriberCount.value} Members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Row {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(64.dp)
                    .clickable {
                        viewModel.searchMode.value = true
                    }, contentDescription = ""
            )
            // Info icon
            Icon(
                imageVector = if (viewModel.subscribed.value) {
                    Icons.Rounded.NotificationsActive
                } else Icons.Outlined.NotificationsOff,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
                    .height(64.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.showSubsDialog.value = true
                    },
                contentDescription = ""
            )
        }
    }
}
