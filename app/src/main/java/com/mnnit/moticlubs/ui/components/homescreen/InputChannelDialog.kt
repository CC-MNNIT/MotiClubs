package com.mnnit.moticlubs.ui.components.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun InputChannelDialog(viewModel: HomeScreenViewModel, onClick: () -> Unit) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showAddChannelDialog = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "New Channel",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

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
                            name = viewModel.inputChannelName
                        )
                        onClick()
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.inputChannelName.isNotEmpty()
                ) {
                    Text(text = "Add Channel", fontSize = 14.sp)
                }
            }
        }
    }
}
