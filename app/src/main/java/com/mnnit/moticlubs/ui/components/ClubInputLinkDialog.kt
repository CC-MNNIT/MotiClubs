package com.mnnit.moticlubs.ui.components

import android.util.Patterns
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun InputLinkDialog(
    showDialog: MutableState<Boolean>,
    inputLinkName: MutableState<String>,
    inputLink: MutableState<String>,
    postMsg: MutableState<TextFieldValue>
) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { showDialog.value = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Link Input",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputLinkName.value,
                    onValueChange = { inputLinkName.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputLink.value,
                    onValueChange = { inputLink.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        val post = postMsg.value.text
                        val selection = postMsg.value.selection
                        val link = "\n[${inputLinkName.value}](${inputLink.value})\n"
                        postMsg.value = TextFieldValue(
                            post.replaceRange(selection.start, selection.end, link),
                            selection = TextRange(selection.end + link.length, selection.end + link.length)
                        )
                        showDialog.value = false
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = inputLink.value.matches(Patterns.WEB_URL.toRegex())
                ) {
                    Text(text = "Add Link", fontSize = 14.sp)
                }
            }
        }
    }
}
