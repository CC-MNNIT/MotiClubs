package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.PostResponse
import com.mnnit.moticlubs.api.UserDetailResponse
import com.mnnit.moticlubs.toTimeString
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun PostScreen(
    postResponse: PostResponse,
    admin: UserDetailResponse
) {
    MotiClubsTheme(getColorScheme()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(getColorScheme().background)) {
                    AdminProfileIcon(admin = admin)
                    Spacer(modifier = Modifier.width(10.dp))
                    AdminNameTimestamp(post = postResponse, name = admin.name)
                }
                val scroll = rememberScrollState(0)
                Divider(startIndent = 8.dp, thickness = 1.dp, color = Color.Black)
                Spacer(modifier = Modifier.height(15.dp))

                MarkdownText(
                    markdown = postResponse.message,
                    color = contentColorFor(backgroundColor = getColorScheme().background),
                    fontSize = 11.sp,
                    modifier = Modifier.verticalScroll(scroll)
                )
            }
        }
    }
}


@Composable
private fun AdminNameTimestamp(post: PostResponse, name: String) {
    Column(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = post.time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp
        )
    }
}


@Composable
fun AdminProfileIcon(
    admin: UserDetailResponse,
) {
    Image(
        painter = if (admin.avatar.isEmpty()) {
            painterResource(id = R.drawable.outline_account_circle_24)
        } else {
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(admin.avatar)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(Constants.AVATAR)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .build()
            )
        }, contentDescription = "",
        modifier = Modifier
            .clip(CircleShape)
            .size(48.dp)
            .clickable { /* TODO */ }
    )
}

@Preview(showBackground = true)
@Composable
fun PreViewPostScreen() {
    PostScreen(
        postResponse = PostResponse(
            "1233", "The killer feature of `markdown-it` is very effective support of\n" +
                    "[syntax plugins](https://www.npmjs.org/browse/keyword/markdown-it-plugin).\n" +
                    "_Compact style:_\n", 1674582431195, "Hue hue hue", "amit9116260192@gmail.com"
        ), admin = UserDetailResponse("Amit kumar", "amit9116260192@gmail.com", "9116260192", "")
    )
}
