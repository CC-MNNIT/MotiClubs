package com.mnnit.moticlubs.ui.components

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun BottomSheetForm() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val colorScheme = getColorScheme()
    var name by remember { mutableStateOf("") }
    var postMsg by remember { mutableStateOf("") }

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .imePadding()
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(4.dp))
                    .background(contentColorFor(backgroundColor = colorScheme.background))
            ) {
                Text(text = "", modifier = Modifier.padding(12.dp))
            }

            Text(
                text = "Contact Us",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Name") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                        disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .weight(1f),
                    value = postMsg,
                    onValueChange = { postMsg = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Message") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                        disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                    )
                )

                AssistChip(
                    onClick = {
                        keyboardController?.hide()
                        Toast.makeText(context, "No Domain 🤨", Toast.LENGTH_SHORT).show()
                    },
                    label = {
                        Text(
                            text = "Send",
                            fontSize = 14.sp,
                            color = contentColorFor(
                                backgroundColor = if (postMsg.isNotEmpty() && name.isNotEmpty()) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                }
                            )
                        )
                    }, leadingIcon = {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.Send),
                            contentDescription = "",
                            tint = contentColorFor(
                                backgroundColor = if (postMsg.isNotEmpty() && name.isNotEmpty()) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .imePadding()
                        .padding(bottom = 16.dp, top = 16.dp)
                        .align(Alignment.End),
                    shape = RoundedCornerShape(24.dp),
                    colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                    enabled = postMsg.isNotEmpty() && name.isNotEmpty()
                )
            }
        }
    }
}
