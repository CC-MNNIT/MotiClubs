package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.ClubModel

@Composable
fun ClubDetailsScreen(
    clubModel: ClubModel,
) {

    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    scrollState,
                    clubModel,
                )
                UserInfoFields(clubModel, this@BoxWithConstraints.maxHeight)
            }
        }
    }
}

@Composable
private fun UserInfoFields(clubModel: ClubModel, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        NameAndDescription(clubModel)

        ProfileProperty(stringResource(R.string.club_name), clubModel.name)

        ProfileProperty(stringResource(R.string.members_cnt), clubModel.description)

        ProfileProperty(stringResource(R.string.twitter), "https://www.github.com/hackeramitkumar", isLink = true)

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.height((containerHeight - 320.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun NameAndDescription(
    clubModel: ClubModel
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Name(
            clubModel,
            modifier = Modifier.height(32.dp)
        )
        Position(
            clubModel,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}

@Composable
private fun Name(clubModel: ClubModel, modifier: Modifier = Modifier) {
    Text(
        text = clubModel.name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun Position(clubModel: ClubModel, modifier: Modifier = Modifier) {
    Text(
        text = clubModel.description,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    clubModel: ClubModel
) {
    Image(
        modifier = Modifier
            .clip(CircleShape)
            .size(256.dp),
        painter = if (clubModel.avatar.isEmpty()) {
            rememberAsyncImagePainter(model = R.drawable.someone_else)
        } else {
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(clubModel.avatar)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(Constants.AVATAR)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .build()
            )
        },
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}

@Composable
fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider()
        Text(
            text = label,
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val style = if (isLink) {
            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        Text(
            text = value,
            modifier = Modifier.height(24.dp),
            style = style
        )
    }
}

@Composable
fun ProfileError() {
    Text("Some error occured")
}
