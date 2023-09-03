package com.mnnit.moticlubs.ui.components.profilescreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun UserInfo(userModel: User, modifier: Modifier = Modifier) {
    val colorScheme = getColorScheme()

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = userModel.name,
        onValueChange = { },
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "Name") },
        enabled = false,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp),
        value = userModel.email.replace("@mnnit.ac.in", ""),
        onValueChange = { },
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "G-Suite ID") },
        enabled = false,
        trailingIcon = {
            Text(
                text = "@mnnit.ac.in",
                modifier = Modifier.padding(end = 16.dp),
                fontWeight = FontWeight.SemiBold
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledTrailingIconColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp),
        value = userModel.branch,
        onValueChange = {},
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "Course") },
        enabled = false,
        singleLine = false,
        leadingIcon = {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 8.dp, end = 4.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = userModel.course,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize(),
                    textAlign = TextAlign.Center
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLeadingIconColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )
}
