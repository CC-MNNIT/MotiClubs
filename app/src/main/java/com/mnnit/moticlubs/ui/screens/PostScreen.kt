package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.api.PostNotificationModel
import com.mnnit.moticlubs.postRead
import com.mnnit.moticlubs.ui.MarkdownText
import com.mnnit.moticlubs.ui.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun PostScreen(
    postNotificationModel: MutableState<PostNotificationModel>
) {
    LocalContext.current.postRead(postNotificationModel.value.clubID, postNotificationModel.value.postID, true)
    val colorScheme = getColorScheme()
    val scroll = rememberScrollState(0)

    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(2.dp, false)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                    shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Text(
                        postNotificationModel.value.clubName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        AdminProfileIcon(postNotificationModel.value.adminAvatar)
                        Spacer(modifier = Modifier.width(10.dp))
                        AdminNameTimestamp(
                            time = postNotificationModel.value.time,
                            name = postNotificationModel.value.adminName
                        )
                    }
                }

                MarkdownText(
                    markdown = postNotificationModel.value.message,
                    color = contentColorFor(backgroundColor = getColorScheme().background),
                    selectable = true,
                    disableLinkMovementMethod = false,
                    modifier = Modifier
                        .verticalScroll(scroll)
                        .padding(16.dp)
                )
            }
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


@Composable
fun AdminProfileIcon(avatar: String) {
    Image(
        painter = LocalContext.current.getImageUrlPainter(url = avatar), contentDescription = "",
        modifier = Modifier
            .clip(CircleShape)
            .size(56.dp)
    )
}
