package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun ConfirmationDialog(
    showDialog: PublishedState<Boolean>,
    message: String,
    positiveBtnText: String,
    negativeBtnText: String = "Cancel",
    imageVector: ImageVector = Icons.Rounded.Info,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {}
) {
    val colorScheme = getColorScheme()
    AlertDialog(onDismissRequest = {
        showDialog.value = false
    }, text = {
        Text(text = message, fontSize = 16.sp)
    }, confirmButton = {
        TextButton(onClick = {
            showDialog.value = false
            onPositive()
        }) {
            Text(text = positiveBtnText, fontSize = 14.sp, color = colorScheme.primary)
        }
    }, dismissButton = {
        TextButton(onClick = {
            showDialog.value = false
            onNegative()
        }) {
            Text(text = negativeBtnText, fontSize = 14.sp, color = colorScheme.primary)
        }
    }, icon = {
        Icon(
            painter = rememberVectorPainter(image = imageVector),
            contentDescription = "",
            modifier = Modifier.size(36.dp)
        )
    })
}