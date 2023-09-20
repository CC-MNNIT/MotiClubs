package com.mnnit.moticlubs.ui.components.postscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.PostScreenViewModel

@Composable
fun ViewedUsersDialog(
    viewModel: PostScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()
    val scrollState = rememberLazyListState()

    Dialog(
        onDismissRequest = { viewModel.showViewedUserDialog.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Users viewed",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LazyColumn(
                        modifier = Modifier.animateContentSize(),
                        state = scrollState,
                    ) {
                        items(viewModel.viewsList.value.size) {
                            LaunchedEffect(it) {
                                if (!viewModel.userMap.value.containsKey(viewModel.viewsList.value[it].userId)) {
                                    viewModel.getUser(viewModel.viewsList.value[it].userId)
                                }
                            }

                            UserItem(viewModel.userMap.value[viewModel.viewsList.value[it].userId] ?: User())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(user: User) {
    val colorScheme = getColorScheme()

    Card(
        modifier = Modifier
            .safeContentPadding()
            .padding(top = 8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.CenterVertically),
                userModel = user,
                size = 48.dp,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier,
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier,
                    text = user.regNo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
