package com.mnnit.moticlubs.ui.components.addmemberscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AddMemberViewModel


@Composable
fun SelectedMemberDialog(
    viewModel: AddMemberViewModel,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()
    val showConfirmation = remember { publishedStateOf(false) }
    val scrollState = rememberLazyListState()

    if (showConfirmation.value) {
        ConfirmationDialog(
            showDialog = showConfirmation,
            message = "Are you sure you want to delete the channel ?\nThis will delete all the posts in the channel",
            positiveBtnText = "Delete",
            onPositive = onAdd
        )
    }

    Dialog(
        onDismissRequest = { viewModel.showSelectedMemberDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Selected members",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .weight(0.1f),
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false)
                ) {
                    LazyColumn(
                        modifier = Modifier.animateContentSize(),
                        state = scrollState,
                    ) {
                        items(viewModel.selectedUserMap.value.entries.size) {
                            UserItem(viewModel.selectedUserMap.value.entries.elementAt(it).value, viewModel)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.showSelectedMemberDialog = false },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(colorScheme.error)
                    ) {
                        Text(text = "Cancel", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onAdd() },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        enabled = viewModel.selectedUserMap.value.size > 0
                    ) {
                        Text(text = "Add all", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(user: User, viewModel: AddMemberViewModel) {
    val colorScheme = getColorScheme()

    Card(
        modifier = Modifier
            .safeContentPadding()
            .padding(top = 8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.CenterVertically),
                userModel = user,
                size = 48.dp
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier,
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier,
                    text = user.regNo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier
                    .size(42.dp)
                    .padding()
                    .align(Alignment.CenterVertically),
                onClick = {
                    viewModel.selectedUserMap.value.remove(user.userId)
                }
            ) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = "")
            }
        }
    }
}
