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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.PostResponse
import com.mnnit.moticlubs.toTimeString
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostScreen(
    appViewModel: AppViewModel
) {
    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        AdminProfileIcon(
                            appViewModel.adminInfoMap[appViewModel.postModel.value.adminEmail]?.avatar ?: ""
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        AdminNameTimestamp(
                            post = appViewModel.postModel.value,
                            name = appViewModel.adminInfoMap[appViewModel.postModel.value.adminEmail]?.name ?: "NA"
                        )
                    }
                }
                val scroll = rememberScrollState(0)
                Spacer(modifier = Modifier.height(15.dp))

                MarkdownText(
                    markdown = appViewModel.postModel.value.message,
                    color = contentColorFor(backgroundColor = getColorScheme().background),
                    modifier = Modifier
                        .verticalScroll(scroll)
                        .padding(8.dp)
                )
            }
        }
    }
}


@Composable
private fun AdminNameTimestamp(post: PostResponse, name: String) {
    Column(modifier = Modifier
        .padding(start = 8.dp)
        .semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = post.time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}


@Composable
fun AdminProfileIcon(
    avatar: String
) {
    Image(
        painter = if (avatar.isEmpty()) {
            painterResource(id = R.drawable.outline_account_circle_24)
        } else {
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatar)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(Constants.AVATAR)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .build()
            )
        }, contentDescription = "",
        modifier = Modifier
            .clip(CircleShape)
            .size(56.dp)
    )
}
