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
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.screens.ClubScreenViewModel

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: () -> Unit
) {
    if (viewModel.showSubsDialog.value) {
        SubscriptionConfirmationDialog(
            viewModel = viewModel,
            appViewModel = appViewModel,
            subscribe = !viewModel.subscribed.value
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(true, onClick = {
                onNavigateToClubDetails()
            }), horizontalArrangement = Arrangement.SpaceAround
    ) {
        ProfilePicture(
            modifier = Modifier.align(Alignment.CenterVertically),
            url = viewModel.clubModel.value.avatar,
            size = 42.dp
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Channel name
            Text(
                text = viewModel.clubModel.value.name,
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
