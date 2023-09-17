package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun InputChannelDialog(
    viewModel: HomeScreenViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()
    Dialog(
        onDismissRequest = { viewModel.showAddChannelDialog = false },
        DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "New Channel",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                )

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Private channel", modifier = Modifier.align(Alignment.CenterVertically))
                    Switch(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        checked = viewModel.inputChannelPrivate == 1,
                        onCheckedChange = { viewModel.inputChannelPrivate = if (it) 1 else 0 },
                    )
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = viewModel.inputChannelName,
                    onValueChange = { viewModel.inputChannelName = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Channel Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        viewModel.eventChannel = viewModel.eventChannel.copy(
                            channelId = System.currentTimeMillis(),
                            name = viewModel.inputChannelName,
                            private = viewModel.inputChannelPrivate,
                        )
                        onClick()
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.inputChannelName.isTrimmedNotEmpty() &&
                        INPUT_CHANNEL_NAME_SIZE.lengthInRange(viewModel.inputChannelName),
                ) {
                    Text(text = "Add Channel", fontSize = 14.sp)
                }
            }
        }
    }
}
