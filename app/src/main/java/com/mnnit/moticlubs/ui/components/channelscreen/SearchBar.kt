package com.mnnit.moticlubs.ui.components.channelscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.ui.theme.colorScheme

@Composable
fun SearchBar(
    searchMode: PublishedState<Boolean>,
    searchValue: PublishedState<String>,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = {
                keyboardController?.hide()
                searchMode.value = false
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                modifier = Modifier
                    .height(64.dp),
                contentDescription = "",
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
            value = searchValue.value,
            onValueChange = { searchValue.value = it },
            shape = RoundedCornerShape(24.dp),
            placeholder = { Text(text = "Search") },
            trailingIcon = {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { keyboardController?.hide() },
                ) {
                    Icon(painter = rememberVectorPainter(image = Icons.Rounded.Search), contentDescription = "")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(onAny = { keyboardController?.hide() }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorScheme.surfaceColorAtElevation(2.dp),
                focusedBorderColor = colorScheme.surfaceColorAtElevation(2.dp),
            ),
        )
    }
}
