package com.mnnit.moticlubs.ui.components.postscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.components.MarkdownText
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.pullrefresh.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.components.pullrefresh.PullRefreshState
import com.mnnit.moticlubs.ui.components.pullrefresh.pullRefresh
import com.mnnit.moticlubs.ui.components.pullrefresh.rememberPullRefreshState
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.PostScreenViewModel

@Composable
fun PostBottomSheetContent(viewModel: PostScreenViewModel, modifier: Modifier = Modifier) {
    val scrollState = rememberLazyListState()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.loadingReplies.value,
        onRefresh = viewModel::getReplies,
    )

    Surface(
        color = colorScheme.surfaceColorAtElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
        ) {
            Replies(
                viewModel,
                refreshState,
                scrollState,
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),
            )
        }
    }
}

@Composable
private fun Replies(
    viewModel: PostScreenViewModel,
    refreshState: PullRefreshState,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.pullRefresh(refreshState)) {
        PullDownProgressIndicator(visible = viewModel.loadingReplies.value, refreshState = refreshState)

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .animateContentSize(),
            reverseLayout = true,
        ) {
            items(viewModel.replyList.value.size) { index ->
                if (!viewModel.userMap.value.containsKey(viewModel.replyList.value[index].userId)) {
                    viewModel.getUser(viewModel.replyList.value[index].userId)
                }

                Reply(viewModel, viewModel.replyList.value[index], colorScheme)

                LaunchedEffect(index) {
                    if (index == viewModel.replyList.value.size - 1) {
                        viewModel.getReplies(refresh = false)
                    }
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            value = viewModel.replyMsg.value,
            onValueChange = { viewModel.replyMsg.value = it },
            shape = RoundedCornerShape(24.dp),
            label = { Text(text = "Reply") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            enabled = !viewModel.showProgress.value,
            trailingIcon = {
                IconButton(
                    enabled = viewModel.showProgress.value || viewModel.replyMsg.value.isTrimmedNotEmpty(),
                    onClick = {
                        if (viewModel.replyMsg.value.isEmpty()) return@IconButton
                        viewModel.sendReply()
                    },
                ) {
                    Icon(imageVector = Icons.Rounded.Send, contentDescription = "")
                }
            },
        )
    }
}

@Composable
private fun Reply(
    viewModel: PostScreenViewModel,
    reply: Reply,
    colorScheme: ColorScheme,
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onLongClick = {
                    if (reply.userId == viewModel.userId) {
                        viewModel.replyDeleteItem.value = reply
                        viewModel.showConfirmationDeleteDialog.value = true
                    }
                },
                onClick = {},
            ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .fillMaxWidth(),
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.Top),
                userModel = viewModel.userMap.value[reply.userId] ?: User(),
                size = 42.dp,
            )

            Column {
                Row {
                    Text(
                        text = viewModel.userMap.value[reply.userId]?.name ?: "Random User",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (reply.userId == viewModel.postModel.userId) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .height(16.dp)
                                .align(Alignment.CenterVertically),
                            colors = CardDefaults.cardColors(colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(0.dp),
                        ) {
                            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(
                                    text = "OP",
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = reply.time.toTimeString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                MarkdownText(
                    markdown = reply.message,
                    color = contentColorFor(backgroundColor = colorScheme.background),
                    maxLines = 4,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    disableLinkMovementMethod = false,
                    selectable = true,
                )
            }
        }
    }
}
