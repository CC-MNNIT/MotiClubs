package com.mnnit.moticlubs.ui.components.addmemberscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun SearchField(
    searchText: PublishedState<String>,
    enabled: Boolean,
    label: String,
    onValueChange: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {
        AnimatedVisibility(
            visible = searchText.value.isTrimmedNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            IconButton(
                modifier = Modifier.size(42.dp),
                onClick = {
                    searchText.value = ""
                    onValueChange()
                },
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "")
            }
        }
    },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colorScheme = getColorScheme()
    val colors = OutlinedTextFieldDefaults.colors(
        unfocusedTextColor = colorScheme.onSurface,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        unfocusedLeadingIconColor = colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = colorScheme.onSurfaceVariant,
        unfocusedBorderColor = colorScheme.outline,
    )

    BasicTextField(
        value = searchText.value,
        modifier = modifier
            .height(56.dp)
            .semantics(mergeDescendants = true) {}
            .padding(top = 8.dp)
            .defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight,
            ),
        onValueChange = {
            searchText.value = it
            onValueChange()
        },
        enabled = enabled,
        readOnly = false,
        textStyle = TextStyle.Default.copy(fontSize = 13.sp, color = colorScheme.onBackground),
        cursorBrush = SolidColor(colorScheme.primary),
        visualTransformation = VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions.Default,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 3,
        minLines = 1,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = searchText.value,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                label = { Text(text = label) },
                trailingIcon = trailingIcon,
                leadingIcon = leadingIcon,
                singleLine = true,
                enabled = enabled,
                isError = false,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = OutlinedTextFieldDefaults.contentPadding(top = 8.dp, bottom = 8.dp),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = RoundedCornerShape(24.dp),
                    )
                },
            )
        },
    )
}
