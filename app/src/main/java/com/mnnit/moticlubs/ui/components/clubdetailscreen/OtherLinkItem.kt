package com.mnnit.moticlubs.ui.components.clubdetailscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.domain.util.OtherLinkComposeModel
import com.mnnit.moticlubs.domain.util.PublishedList
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.ui.theme.textColorFor

@Composable
fun OtherLinkItem(
    idx: Int,
    linksList: PublishedList<OtherLinkComposeModel>,
    refIdx: PublishedState<Int>,
    showColorPalette: PublishedState<Boolean>,
    modifier: Modifier = Modifier,
    onDeleteItem: (idx: Int) -> Unit = {},
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = linksList.value[idx].fieldValue.value,
            onValueChange = {
                val str = it.text
                if (!str.contains("\\")) {
                    val tr = it.selection
                    val subStr = str.substring(tr.start, tr.end)

                    linksList.value[idx].fieldValue.value = TextFieldValue(
                        str.replaceRange(tr.start, tr.end, "\\$subStr"),
                        selection = TextRange(tr.end + 1, tr.end + 1),
                    )
                } else {
                    linksList.value[idx].fieldValue.value = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(24.dp),
            label = { Text(text = "Name \\ Link") },
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp, end = 8.dp),
                    onClick = {
                        refIdx.value = idx
                        showColorPalette.value = true
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = linksList.value[idx].color.value),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ColorLens,
                        contentDescription = null,
                        tint = textColorFor(color = linksList.value[idx].color.value),
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        linksList.value.removeAt(idx)
                        onDeleteItem(idx)
                    },
                ) {
                    Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
                }
            },
            singleLine = true,
            isError = !linksList.value[idx].validUrl(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        )
    }
}
