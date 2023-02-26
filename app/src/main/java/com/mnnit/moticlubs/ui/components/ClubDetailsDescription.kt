package com.mnnit.moticlubs.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ClubDetailsScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun DescriptionComponent(viewModel: ClubDetailsScreenViewModel) {
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val colorScheme = getColorScheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            Text(
                "Description",
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 8.dp)
            )
            if (viewModel.isAdmin) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        // ON SAVE
                        if (viewModel.editDescriptionMode) {
                            keyboardController?.hide()

                            viewModel.progressMsg = "Updating"
                            viewModel.showProgressDialog.value = true
                            viewModel.updateClub(
                                description = viewModel.displayedDescription,
                                onResponse = {
                                    viewModel.showProgressDialog.value = false
                                    viewModel.editDescriptionMode = false
                                    viewModel.displayedDescription = viewModel.clubModel.description
                                },
                                onFailure = {
                                    viewModel.showProgressDialog.value = false
                                    Toast.makeText(context, "Unable to update description", Toast.LENGTH_SHORT).show()
                                })
                        } else {
                            // ON EDIT
                            viewModel.displayedDescription = viewModel.clubModel.description
                            viewModel.editDescriptionMode = true
                        }
                    },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = if (viewModel.editDescriptionMode) {
                            Icons.Rounded.Save
                        } else {
                            Icons.Rounded.Edit
                        }, contentDescription = ""
                    )
                }
                if (viewModel.editDescriptionMode) {
                    IconButton(
                        onClick = {
                            viewModel.editDescriptionMode = false
                            viewModel.displayedDescription = viewModel.clubModel.description
                            keyboardController?.hide()
                        },
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
                    }
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .focusRequester(focusRequester)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent { state ->
                    if (state.isFocused) {
                        scope.launch { bringIntoViewRequester.bringIntoView() }
                    }
                },
            value = viewModel.displayedDescription,
            onValueChange = { str ->
                viewModel.displayedDescription = str
                scope.launch { bringIntoViewRequester.bringIntoView() }
            },
            shape = RoundedCornerShape(24.dp),
            enabled = viewModel.editDescriptionMode,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledBorderColor = colorScheme.background,
                focusedLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                focusedBorderColor = colorScheme.background,
                unfocusedLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                unfocusedBorderColor = colorScheme.background
            )
        )
        LaunchedEffect(viewModel.editDescriptionMode) {
            if (viewModel.editDescriptionMode) focusRequester.requestFocus()
        }
    }
}
