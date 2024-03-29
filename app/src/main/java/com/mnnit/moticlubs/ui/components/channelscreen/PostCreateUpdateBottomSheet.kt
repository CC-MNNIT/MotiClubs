package com.mnnit.moticlubs.ui.components.channelscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.MarkdownRender
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.isExpanded
import com.mnnit.moticlubs.ui.components.isPartiallyExpanded
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun PostCreateUpdateBottomSheet(
    viewModel: ChannelScreenViewModel,
    onNavigateImageClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        color = colorScheme.surfaceColorAtElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        if (viewModel.showProgress.value) {
            ProgressDialog(progressMsg = viewModel.progressText.value)
        }

        if (viewModel.showEditDialog.value) {
            PostConfirmationDialog(viewModel = viewModel, update = true) {
                viewModel.updatePost()
                scope.launch {
                    if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                        viewModel.bottomSheetScaffoldState.value.bottomSheetState.partialExpand()
                    }
                }
            }
        }

        if (viewModel.showDialog.value) {
            PostConfirmationDialog(viewModel = viewModel, update = false) {
                viewModel.sendPost()
                scope.launch {
                    if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                        viewModel.bottomSheetScaffoldState.value.bottomSheetState.partialExpand()
                    }
                }
            }
        }

        if (viewModel.showClearDraftDialog.value) {
            ConfirmationDialog(
                showDialog = viewModel.showClearDraftDialog,
                message = "Are you sure you want to clear draft ?",
                positiveBtnText = "Clear",
                onPositive = {
                    viewModel.clearEditor()
                    scope.launch {
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.partialExpand()
                        }
                    }
                },
            )
        }
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
                .imePadding()
                .fillMaxWidth(),
        ) {
            AnimatedVisibility(
                visible = viewModel.isPreviewMode.value,
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .weight(1f),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    MarkdownRender(
                        mkd = viewModel.eventPostMsg.value.text,
                        imageReplacerMap = viewModel.eventImageReplacerMap,
                        onImageClick = onNavigateImageClick,
                    )
                }
            }

            AnimatedVisibility(
                visible = !viewModel.isPreviewMode.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .imePadding(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .weight(1f)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                scope.launch {
                                    if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isPartiallyExpanded) {
                                        viewModel.scrollValue.intValue = scrollState.value
                                        viewModel.bottomSheetScaffoldState.value.bottomSheetState.expand()
                                    }
                                }
                            }
                        },
                    value = viewModel.eventPostMsg.value,
                    onValueChange = { viewModel.eventPostMsg.value = it },
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text(text = "Write your message here\nSupports Markdown formatting") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
            }

            PostTextFormatter(viewModel = viewModel)

            Row(
                modifier = Modifier
                    .imePadding()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                FilterChip(
                    selected = viewModel.isPreviewMode.value,
                    onClick = {
                        keyboardController?.hide()
                        if (!viewModel.isPreviewMode.value) {
                            scope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        }
                        viewModel.isPreviewMode.value = !viewModel.isPreviewMode.value
                    },
                    label = {
                        Text(text = "Preview", fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Visibility),
                            contentDescription = "",
                        )
                    },
                    modifier = Modifier
                        .imePadding()
                        .align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(24.dp),
                )

                Spacer(Modifier.weight(1f))

                AssistChip(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (viewModel.editMode.value) {
                            viewModel.showEditDialog.value = true
                        } else {
                            viewModel.showDialog.value = true
                        }
                    },
                    label = {
                        Text(
                            text = if (viewModel.editMode.value) "Update" else "Send",
                            fontSize = 14.sp,
                            color = contentColorFor(
                                backgroundColor = if (
                                    viewModel.eventPostMsg.value.text.isTrimmedNotEmpty() &&
                                    viewModel.postLengthInRange()
                                ) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                            ),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Send),
                            contentDescription = "",
                            tint = contentColorFor(
                                backgroundColor = if (
                                    viewModel.eventPostMsg.value.text.isTrimmedNotEmpty() &&
                                    viewModel.postLengthInRange()
                                ) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                            ),
                        )
                    },
                    modifier = Modifier
                        .imePadding()
                        .align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(24.dp),
                    colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                    enabled = viewModel.eventPostMsg.value.text.isTrimmedNotEmpty() &&
                        viewModel.postLengthInRange(),
                )
            }
        }
    }
}

@Composable
private fun PostConfirmationDialog(viewModel: ChannelScreenViewModel, update: Boolean, onPost: () -> Unit) {
    ConfirmationDialog(
        showDialog = if (update) viewModel.showEditDialog else viewModel.showDialog,
        message = "${if (update) "Update post" else "Post"} message in ${viewModel.clubModel.name} ?",
        positiveBtnText = if (update) "Update" else "Post",
        imageVector = Icons.Outlined.Article,
        onPositive = {
            viewModel.progressText.value = if (update) "Updating ..." else "Posting ..."
            viewModel.showProgress.value = true
            onPost()
        },
    )
}
