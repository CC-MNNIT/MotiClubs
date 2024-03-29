package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.domain.util.Constants.INPUT_CHANNEL_NAME_SIZE
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.domain.util.lengthInRange
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelDetailScreenViewModel

@Composable
fun UpdateChannelDialog(
    viewModel: ChannelDetailScreenViewModel,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showConfirmation = remember { publishedStateOf(false) }

    if (showConfirmation.value) {
        ConfirmationDialog(
            showDialog = showConfirmation,
            message = "Are you sure you want to delete the channel ?\nThis will delete all the posts in the channel",
            positiveBtnText = "Delete",
            onPositive = onDelete,
        )
    }

    Dialog(
        onDismissRequest = { viewModel.showUpdateChannelDialog.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Update Channel",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        value = viewModel.updateChannelName,
                        onValueChange = { viewModel.updateChannelName = it },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(text = "Channel Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    )

                    IconButton(
                        modifier = Modifier
                            .weight(0.2f)
                            .align(Alignment.CenterVertically),
                        onClick = { showConfirmation.value = true },
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "",
                            tint = colorScheme.error,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = { viewModel.showUpdateChannelDialog.value = false },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(colorScheme.error),
                    ) {
                        Text(text = "Cancel", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onUpdate() },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterVertically),
                        enabled = viewModel.updateChannelName.isTrimmedNotEmpty() &&
                            INPUT_CHANNEL_NAME_SIZE.lengthInRange(viewModel.updateChannelName),
                    ) {
                        Text(text = "Save", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
