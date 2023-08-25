package com.mnnit.moticlubs.ui.components.clubscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: (clubModel: Club, user: User) -> Unit,
    onBackPressed: () -> Unit
) {
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
                text = if (viewModel.memberCount.intValue == -1) "General" else {
                    "${viewModel.memberCount.intValue} members"
                },
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

            if (viewModel.isAdmin) {
                // Info icon
                Icon(
                    imageVector = Icons.Outlined.GroupAdd,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp)
                        .height(64.dp)
                        .clip(CircleShape)
                        .clickable {
//                            viewModel.showSubsDialog.value = true
                        },
                    contentDescription = ""
                )
            }
        }
    }
}
