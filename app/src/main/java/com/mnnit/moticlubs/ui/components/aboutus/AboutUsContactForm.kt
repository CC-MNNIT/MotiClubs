package com.mnnit.moticlubs.ui.components.aboutus

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
import com.mnnit.moticlubs.ui.theme.colorScheme

@Composable
fun AboutUsContactForm(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var name by remember { publishedStateOf("") }
    var postMsg by remember { publishedStateOf("") }

    Surface(
        color = colorScheme.surfaceColorAtElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .padding(top = 1.dp, start = 16.dp, end = 16.dp)
                .imePadding()
                .fillMaxWidth(),
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
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                    disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                ),
                singleLine = true,
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
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                    disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                ),
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
                            backgroundColor = if (postMsg.isTrimmedNotEmpty() && name.isTrimmedNotEmpty()) {
                                colorScheme.primary
                            } else {
                                colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        ),
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        painter = rememberVectorPainter(image = Icons.Rounded.Send),
                        contentDescription = "",
                        tint = contentColorFor(
                            backgroundColor = if (postMsg.isTrimmedNotEmpty() && name.isTrimmedNotEmpty()) {
                                colorScheme.primary
                            } else {
                                colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        ),
                    )
                },
                modifier = Modifier
                    .imePadding()
                    .padding(bottom = 16.dp, top = 16.dp)
                    .align(Alignment.End),
                shape = RoundedCornerShape(24.dp),
                colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                enabled = postMsg.isTrimmedNotEmpty() && name.isTrimmedNotEmpty(),
            )
        }
    }
}
