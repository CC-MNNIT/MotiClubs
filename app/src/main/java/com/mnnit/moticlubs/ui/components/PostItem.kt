package com.mnnit.moticlubs.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.util.getUnreadPost
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.theme.getColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostItem(
    bottomSheetScaffoldState: MutableState<BottomSheetScaffoldState>,
    clubModel: Club,
    channelModel: Channel,
    postsList: SnapshotStateList<Post>,
    userID: Long,
    idx: Int,
    admin: User,
    editMode: MutableState<Boolean>,
    eventUpdatePost: MutableState<Post>,
    postMsg: MutableState<TextFieldValue>,
    imageReplacerMap: MutableMap<String, String>,
    eventDeletePost: MutableState<Post>,
    showDelPostDialog: MutableState<Boolean>,
    onNavigateToPost: (post: PostNotificationModel) -> Unit
) {
    val scope = rememberCoroutineScope()
    val colorScheme = getColorScheme()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp), elevation = CardDefaults.cardElevation(0.dp),
        onClick = {
            onNavigateToPost(
                PostNotificationModel(
                    clubModel.name,
                    channelModel.name,
                    channelModel.channelId,
                    postsList[idx].postId,
                    userID,
                    admin.name,
                    admin.avatar,
                    postsList[idx].message,
                )
            )
        },
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
    ) {
        Card(
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ProfilePicture(
                    modifier = Modifier.align(Alignment.Top),
                    url = admin.avatar,
                    size = 42.dp
                )

                AuthorNameTimestamp(postsList[idx].postId, admin.name)
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = LocalContext.current.getUnreadPost(channelModel.channelId)
                        .contains(postsList[idx].postId.toString()),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                ) {
                    BadgedBox(badge = { Badge { } }) {}
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(visible = postsList[idx].userId == userID) {
                    IconButton(onClick = {
                        eventUpdatePost.value = postsList[idx]
                        editMode.value = true

                        var preprocessText = postsList[idx].message
                        imageReplacerMap.clear()
                        postsList[idx].message.lines().forEach {
                            if (it.startsWith("<img src")) {
                                val key = "[image_${imageReplacerMap.size}]"
                                imageReplacerMap[key] = it
                                preprocessText = preprocessText.replace(it, key)
                            }
                        }
                        postMsg.value = TextFieldValue(preprocessText)
                        scope.launch {
                            if (bottomSheetScaffoldState.value.bottomSheetState.isCollapsed) {
                                bottomSheetScaffoldState.value.bottomSheetState.expand()
                            }
                        }
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = ""
                        )
                    }
                }
                AnimatedVisibility(visible = postsList[idx].userId == userID) {
                    IconButton(onClick = {
                        eventDeletePost.value = postsList[idx]
                        showDelPostDialog.value = true
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = ""
                        )
                    }
                }
            }
        }
        MarkdownText(
            markdown = postsList[idx].message,
            color = contentColorFor(backgroundColor = getColorScheme().background),
            maxLines = 4,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 8.dp),
            disableLinkMovementMethod = true
        )
    }
}

@Composable
private fun AuthorNameTimestamp(time: Long, name: String) {
    Column(modifier = Modifier
        .padding(start = 16.dp)
        .semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
