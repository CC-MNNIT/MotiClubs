package com.mnnit.moticlubs.ui.components.postscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.util.getUserID
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.components.MarkdownText
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.PullDownProgressIndicator
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

            Row(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "Replies",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            scope.launch { viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse() }
                        }
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isCollapsed) {
                            scope.launch { viewModel.bottomSheetScaffoldState.value.bottomSheetState.expand() }
                        }
                    }
                ) {
                    Icon(
                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isExpanded) {
                            Icons.Rounded.KeyboardArrowDown
                        } else {
                            Icons.Rounded.KeyboardArrowUp
                        }, contentDescription = "", tint = colorScheme.primary
                    )
                }
            }

            Replies(
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),
                viewModel,
                refreshState,
                scrollState
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Replies(
    modifier: Modifier = Modifier,
    viewModel: PostScreenViewModel,
    refreshState: PullRefreshState,
    scrollState: LazyListState
) {
    val colorScheme = getColorScheme()

    Column(modifier = modifier) {

        PullDownProgressIndicator(visible = viewModel.loadingReplies.value, refreshState = refreshState)

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .animateContentSize(),
            reverseLayout = true,
        ) {
            items(viewModel.replyList.size) {
                if (!viewModel.userMap.containsKey(viewModel.replyList[it].userId)) {
                    viewModel.getUser(viewModel.replyList[it].userId)
                }

                Reply(viewModel, viewModel.replyList[it], colorScheme)
            }

            item {
                LaunchedEffect(scrollState.canScrollForward) {
                    if (!scrollState.canScrollForward && !viewModel.loadingReplies.value && !viewModel.pageEnded) {
                        viewModel.getReplies(refresh = false)
                    }
                }
            }
        }

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
                        viewModel.sendReply()
                    }) {
                    Icon(imageVector = Icons.Rounded.Send, contentDescription = "")
                }
            }
        )
    }
}

@Composable
private fun Reply(
    viewModel: PostScreenViewModel,
    reply: Reply,
    colorScheme: ColorScheme
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .safeContentPadding()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(onLongClick = {
                if (reply.userId == context.getUserID()) {
                    viewModel.replyDeleteItem.value = reply
                    viewModel.showConfirmationDeleteDialog.value = true
                }
            }, onClick = {}),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.Top),
                url = viewModel.userMap[reply.userId]?.avatar ?: "",
                size = 42.dp
            )

            Column {
                Row {
                    Text(
                        text = viewModel.userMap[reply.userId]?.name ?: "Random User",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = reply.time.toTimeString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }

                MarkdownText(
                    markdown = reply.message,
                    color = contentColorFor(backgroundColor = getColorScheme().background),
                    maxLines = 4,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    disableLinkMovementMethod = true
                )
            }
        }
    }
}
