package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: (clubModel: Club, user: User) -> Unit,
    onBackPressed: () -> Unit
) {
    if (viewModel.showSubsDialog.value) {
        val subscribe = !viewModel.userSubscribed.value
        ConfirmationDialog(
            showDialog = viewModel.showSubsDialog,
            message = "Are you sure you want to ${if (subscribe) "subscribe" else "unsubscribe"} ?",
            positiveBtnText = if (subscribe) "Subscribe" else "Unsubscribe",
            imageVector = if (subscribe) Icons.Rounded.NotificationsActive else Icons.Outlined.NotificationsOff,
            onPositive = {
                viewModel.progressText.value = if (subscribe) "Subscribing ..." else "Unsubscribing ..."
                viewModel.subscribeToClub(subscribe)
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(true, onClick = { onNavigateToClubDetails(viewModel.clubModel, viewModel.userModel) }),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            onClick = { onBackPressed() }
        ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
        }

        ProfilePicture(
            modifier = Modifier.align(Alignment.CenterVertically),
            url = viewModel.clubModel.avatar,
            size = 42.dp
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Channel name
            Text(
                text = "${viewModel.clubModel.name} - ${viewModel.channelModel.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )

            // Number of members
            Text(
                text = "${viewModel.subscriberList.size} Members",
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
                imageVector = if (viewModel.userSubscribed.value) {
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
