package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.util.postRead
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.MarkdownRender
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.postscreen.PostBottomSheetContent
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.PostScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostScreen(
    onNavigateImageClick: (url: String) -> Unit,
    onNavigateBackPressed: () -> Unit,
    viewModel: PostScreenViewModel = hiltViewModel()
) {
    LocalContext.current.postRead(
        viewModel.postNotificationModel.channelId,
        viewModel.postNotificationModel.postId,
        true
    )

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(elevation = 2.dp, true)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            androidx.compose.material.BottomSheetScaffold(
                modifier = Modifier.imePadding(),
                sheetContent = { PostBottomSheetContent(viewModel = viewModel) },
                content = {
                    Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
                        Column(modifier = Modifier.fillMaxWidth()) {

                            if (viewModel.showDialog.value) {
                                ProgressDialog(progressMsg = "Replying...")
                            }

                            if (viewModel.showDeleteDialog.value) {
                                ProgressDialog(progressMsg = "Deleting...")
                            }

                            if (viewModel.showConfirmationDeleteDialog.value) {
                                ConfirmationDialog(
                                    showDialog = viewModel.showConfirmationDeleteDialog,
                                    message = "Are you sure you want to delete this reply ?",
                                    positiveBtnText = "Delete",
                                    imageVector = Icons.Rounded.DeleteForever,
                                    onPositive = viewModel::deleteReply,
                                    onNegative = { viewModel.replyDeleteItem.value = Reply() }
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                                shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                                elevation = CardDefaults.cardElevation(0.dp),
                            ) {
                                Row(modifier = Modifier.padding(top = 16.dp)) {
                                    IconButton(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(start = 16.dp)
                                            .size(24.dp),
                                        onClick = { onNavigateBackPressed() }
                                    ) {
                                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                                    }

                                    Text(
                                        "${viewModel.postNotificationModel.clubName} - ${viewModel.postNotificationModel.channelName}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .align(Alignment.CenterVertically),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    ProfilePicture(url = viewModel.postNotificationModel.adminAvatar, size = 56.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    AdminNameTimestamp(
                                        time = viewModel.postNotificationModel.postId.toTimeString(),
                                        name = viewModel.postNotificationModel.adminName
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 16.dp, bottom = 16.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Visibility, contentDescription = "")
                                        Text(
                                            modifier = Modifier.padding(start = 8.dp),
                                            text = viewModel.viewCount,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            MarkdownRender(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                                mkd = viewModel.postNotificationModel.message,
                                selectable = true,
                                disableLinkMovementMethod = true,
                                onImageClick = onNavigateImageClick
                            )
                        }
                    }
                },
                scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = 72.dp,
                sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp),
                sheetGesturesEnabled = viewModel.bottomSheetScaffoldState.value.bottomSheetState.isCollapsed
            )
        }
    }
}

@Composable
private fun AdminNameTimestamp(time: String, name: String) {
    Column(modifier = Modifier
        .padding(start = 8.dp)
        .semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
