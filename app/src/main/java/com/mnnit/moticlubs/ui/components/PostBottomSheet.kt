package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.PostScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostBottomSheetContent(viewModel: PostScreenViewModel) {
    val scope = rememberCoroutineScope()
    val colorScheme = getColorScheme()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.loadingReplies.value,
        onRefresh = viewModel::getReplies
    )

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
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

            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(
                    text = "Replies",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()

                    scope.launch {
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse()
                        }
                    }
                }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(Icons.Rounded.Close, contentDescription = "", tint = colorScheme.primary)
                }
            }

            Replies(
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),
                viewModel,
                scrollState
            )

            PullDownProgressIndicator(visible = viewModel.loadingReplies.value, refreshState = refreshState)

            OutlinedTextField(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                value = viewModel.replyMsg.value,
                onValueChange = { viewModel.replyMsg.value = it },
                shape = RoundedCornerShape(24.dp),
                label = { Text(text = "Reply") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                enabled = !viewModel.showProgress.value,
                trailingIcon = {
                    IconButton(
                        enabled = viewModel.showProgress.value || viewModel.replyMsg.value.isNotEmpty(),
                        onClick = {
                            if (viewModel.replyMsg.value.isEmpty()) return@IconButton
                            viewModel.sendReply(-1)
                        }) {
                        Icon(imageVector = Icons.Rounded.Send, contentDescription = "")
                    }
                }
            )
        }
    }
}

@Composable
private fun Replies(modifier: Modifier = Modifier, viewModel: PostScreenViewModel, scrollState: LazyListState) {
    val colorScheme = getColorScheme()

    Box(modifier = modifier) {
        LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize(), reverseLayout = true) {
            items(viewModel.replyList.size) {
                Card(
                    modifier = Modifier.padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        ProfilePicture(
                            modifier = Modifier.align(Alignment.Top),
                            url = viewModel.userMap[viewModel.replyList[viewModel.replyList.size - 1 - it].userID]!!.avatar,
                            size = 42.dp
                        )

                        Column {
                            Row {
                                Text(
                                    text = viewModel.userMap[viewModel.replyList[viewModel.replyList.size - 1 - it].userID]!!.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 14.sp,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = viewModel.replyList[viewModel.replyList.size - it - 1].time.toTimeString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp
                                )
                            }

                            MarkdownText(
                                markdown = viewModel.replyList[viewModel.replyList.size - it - 1].message,
                                color = contentColorFor(backgroundColor = getColorScheme().background),
                                maxLines = 4,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                disableLinkMovementMethod = true
                            )
                        }
                    }
                }
            }
        }
    }
}
