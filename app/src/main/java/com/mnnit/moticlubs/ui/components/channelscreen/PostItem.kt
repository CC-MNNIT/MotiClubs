package com.mnnit.moticlubs.ui.components.channelscreen

import android.util.Patterns
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.util.getUnreadPost
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.components.MarkdownText
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.getColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostItem(
    bottomSheetScaffoldState: MutableState<BottomSheetScaffoldState>,
    channelModel: Channel,
    post: Post,
    userId: Long,
    admin: AdminUser,
    editMode: MutableState<Boolean>,
    eventUpdatePost: MutableState<Post>,
    postMsg: MutableState<TextFieldValue>,
    imageReplacerMap: MutableMap<String, String>,
    eventDeletePost: MutableState<Post>,
    showDelPostDialog: MutableState<Boolean>,
    onNavigateToPost: (postId: Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    val colorScheme = getColorScheme()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp), elevation = CardDefaults.cardElevation(0.dp),
        onClick = { onNavigateToPost(post.postId) },
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
                    userModel = admin.getUser(),
                    size = 42.dp
                )

                AuthorNameTimestamp(post.postId, admin.name)
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = LocalContext.current.getUnreadPost(channelModel.channelId)
                        .contains(post.postId.toString()),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                ) {
                    Badge { }
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(visible = post.userId == userId) {
                    IconButton(onClick = {
                        eventUpdatePost.value = post
                        editMode.value = true

                        var preprocessText = post.message
                        imageReplacerMap.clear()
                        post.message.lines().forEach {
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
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = ""
                        )
                    }
                }
                AnimatedVisibility(visible = post.userId == userId) {
                    IconButton(onClick = {
                        eventDeletePost.value = post
                        showDelPostDialog.value = true
                    }) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = ""
                        )
                    }
                }
            }
        }
        MarkdownText(
            markdown = post.message.replace("<img src=\"${Patterns.WEB_URL.pattern()}\">".toRegex(), "_image 📸_"),
            color = contentColorFor(backgroundColor = getColorScheme().background),
            maxLines = 4,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 8.dp),
            disableLinkMovementMethod = true,
        )
    }
}

@Composable
private fun AuthorNameTimestamp(time: Long, name: String) {
    Column(modifier = Modifier
        .padding(start = 16.dp)
        .semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
