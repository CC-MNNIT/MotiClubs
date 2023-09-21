package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.util.toTimeString
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.MarkdownRender
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.postscreen.PostBottomSheetContent
import com.mnnit.moticlubs.ui.components.postscreen.ViewedUsersDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.PostScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostScreen(
    onNavigateImageClick: (url: String) -> Unit,
    onNavigateBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostScreenViewModel = hiltViewModel(),
) {
    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetTransparentSystemBars(setStatusBar = false, setNavBar = false)
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            color = colorScheme.background,
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
                                    onNegative = { viewModel.replyDeleteItem.value = Reply() },
                                )
                            }

                            if (viewModel.showViewedUserDialog.value) {
                                ViewedUsersDialog(viewModel = viewModel)
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
                                        onClick = { onNavigateBackPressed() },
                                    ) {
                                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                                    }

                                    Text(
                                        "${viewModel.clubModel.name} - ${viewModel.channelModel.name}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .align(Alignment.CenterVertically),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                ) {
                                    ProfilePicture(userModel = viewModel.userModel, size = 56.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    AdminNameTimestamp(
                                        time = viewModel.postId.toTimeString(),
                                        name = viewModel.userModel.name,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 16.dp, bottom = 16.dp),
                                        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                                        onClick = { viewModel.showViewedUserDialog.value = true },
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 4.dp)) {
                                            Icon(imageVector = Icons.Outlined.Visibility, contentDescription = "")
                                            Text(
                                                modifier = Modifier.padding(start = 8.dp),
                                                text = viewModel.viewCount,
                                                fontSize = 14.sp,
                                            )
                                        }
                                    }
                                }
                            }

                            MarkdownRender(
                                modifier = Modifier.fillMaxWidth(),
                                mkd = viewModel.postModel.message,
                                selectable = true,
                                disableLinkMovementMethod = false,
                                onImageClick = onNavigateImageClick,
                            )
                        }
                    }
                },
                scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = 72.dp,
                sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp),
                sheetGesturesEnabled = viewModel.bottomSheetScaffoldState.value.bottomSheetState.isCollapsed,
            )
        }
    }
}

@Composable
private fun AdminNameTimestamp(time: String, name: String) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
        )
    }
}
