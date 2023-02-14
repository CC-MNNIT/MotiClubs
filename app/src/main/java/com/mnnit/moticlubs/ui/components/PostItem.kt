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
import com.mnnit.moticlubs.getUnreadPost
import com.mnnit.moticlubs.network.model.AdminDetailResponse
import com.mnnit.moticlubs.network.model.ClubNavModel
import com.mnnit.moticlubs.network.model.PostDto
import com.mnnit.moticlubs.network.model.PostNotificationModel
import com.mnnit.moticlubs.toTimeString
import com.mnnit.moticlubs.ui.theme.getColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostItem(
    bottomSheetScaffoldState: MutableState<BottomSheetScaffoldState>,
    clubNavModel: ClubNavModel,
    postsList: SnapshotStateList<PostDto>,
    userID: Int,
    idx: Int,
    admin: AdminDetailResponse,
    editMode: MutableState<Boolean>,
    editPostIdx: MutableState<Int>,
    postMsg: MutableState<TextFieldValue>,
    delPostIdx: MutableState<Int>,
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
                    clubNavModel.name,
                    clubNavModel.channel.name,
                    clubNavModel.channel.id,
                    postsList[idx].postID,
                    admin.name,
                    admin.avatar,
                    postsList[idx].message,
                    postsList[idx].time.toTimeString()
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

                AuthorNameTimestamp(postsList[idx].time, admin.name)
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = LocalContext.current.getUnreadPost(clubNavModel.channel.id)
                        .contains(postsList[idx].postID.toString()),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                ) {
                    BadgedBox(badge = { Badge { } }) {}
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(visible = postsList[idx].userID == userID) {
                    IconButton(onClick = {
                        editPostIdx.value = idx
                        editMode.value = true
                        postMsg.value =
                            TextFieldValue(postsList[idx].message.replace("<br>\n", "\n"))
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
                AnimatedVisibility(visible = postsList[idx].userID == userID) {
                    IconButton(onClick = {
                        delPostIdx.value = idx
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
