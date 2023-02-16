package com.mnnit.moticlubs.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(viewModel: ClubScreenViewModel, onNavigateImageClick: (url: String) -> Unit) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val colorScheme = getColorScheme()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        if (viewModel.showProgress.value) {
            ProgressDialog(progressMsg = viewModel.progressText.value)
        }

        if (viewModel.showEditDialog.value) {
            PostConfirmationDialog(viewModel = viewModel, update = true) {
                viewModel.isPreviewMode.value = false

                var text = viewModel.postMsg.value.text
                viewModel.imageReplacerMap.forEach { (key, value) ->
                    text = text.replace(key.replace("\n", ""), value)
                }

                viewModel.updatePost(viewModel.postsList[viewModel.editPostIdx.value].postID, text, {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                    viewModel.editMode.value = false
                    viewModel.showProgress.value = false

                    viewModel.postMsg.value = TextFieldValue("")
                    viewModel.imageReplacerMap.clear()
                    viewModel.fetchPostsList()
                    scope.launch {
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse()
                        }
                    }
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error updating msg", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (viewModel.showDialog.value) {
            PostConfirmationDialog(viewModel = viewModel, update = false) {
                viewModel.isPreviewMode.value = false

                var text = viewModel.postMsg.value.text
                viewModel.imageReplacerMap.forEach { (key, value) ->
                    text = text.replace(key.replace("\n", ""), value)
                }

                viewModel.sendPost(text, {
                    Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                    viewModel.showProgress.value = false
                    viewModel.editMode.value = false

                    viewModel.postMsg.value = TextFieldValue("")
                    viewModel.imageReplacerMap.clear()
                    viewModel.fetchPostsList()
                    scope.launch {
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse()
                        }
                    }
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error posting msg", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp, end = 16.dp
                )
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

            Row(modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)) {
                Text(
                    text = if (viewModel.editMode.value) "Update Post" else "Write Post",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()

                    viewModel.postMsg.value = TextFieldValue("")
                    viewModel.imageReplacerMap.clear()
                    viewModel.editMode.value = false

                    scope.launch {
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse()
                        }
                    }
                }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(Icons.Rounded.Close, contentDescription = "", tint = colorScheme.primary)
                }
            }

            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                AnimatedVisibility(
                    visible = viewModel.isPreviewMode.value,
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth()
                        .weight(1f),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .imePadding()
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        MarkdownRender(
                            mkd = viewModel.postMsg.value.text, imageReplacerMap = viewModel.imageReplacerMap,
                            onImageClick = onNavigateImageClick
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !viewModel.isPreviewMode.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .imePadding(),
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .weight(1f)
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    scope.launch {
                                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isCollapsed) {
                                            viewModel.scrollValue.value = scrollState.value
                                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.expand()
                                        }
                                    }
                                }
                            },
                        value = viewModel.postMsg.value,
                        onValueChange = { viewModel.postMsg.value = it },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(text = "Post") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                        )
                    )
                }

                TextFormatter(viewModel = viewModel)

                Row(
                    modifier = Modifier
                        .imePadding()
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
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
                        }, leadingIcon = {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Visibility),
                                contentDescription = ""
                            )
                        }, modifier = Modifier
                            .imePadding()
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(24.dp)
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
                                    backgroundColor = if (viewModel.postMsg.value.text.isNotEmpty()) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            )
                        }, leadingIcon = {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Send),
                                contentDescription = "",
                                tint = contentColorFor(
                                    backgroundColor = if (viewModel.postMsg.value.text.isNotEmpty()) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            )
                        }, modifier = Modifier
                            .imePadding()
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(24.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                        enabled = viewModel.postMsg.value.text.isNotEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun PostConfirmationDialog(viewModel: ClubScreenViewModel, update: Boolean, onPost: () -> Unit) {
    ConfirmationDialog(
        showDialog = if (update) viewModel.showEditDialog else viewModel.showDialog,
        message = "${if (update) "Update post" else "Post"} message in ${viewModel.clubNavModel.name} ?",
        positiveBtnText = if (update) "Update" else "Post",
        imageVector = Icons.Outlined.Article,
        onPositive = {
            viewModel.progressText.value = if (update) "Updating ..." else "Posting ..."
            viewModel.showProgress.value = true
            onPost()
        }
    )
}
