package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.ui.activity.AppScreenMode
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme



@Composable
fun HomeScreen(appViewModel: AppViewModel) {
    MotiClubsTheme(getColorScheme()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(Alignment.Top),
        ) {
            ProfileIcon(appViewModel = appViewModel, modifier = Modifier.align(Alignment.End))

            Text(
                text = "MNNIT Clubs", fontSize = 28.sp
            )
        }
    }
}

@Composable
fun ProfileIcon(appViewModel: AppViewModel, modifier: Modifier) {
    if (appViewModel.avatar.value.isEmpty()) {
        Image(
            painter = rememberVectorPainter(image = Icons.Outlined.AccountCircle),
            contentDescription = "",
            modifier = modifier
                .clip(CircleShape)
                .size(48.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    appViewModel.appScreenMode.value = AppScreenMode.LOGIN
                }
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(appViewModel.avatar.value)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(Constants.AVATAR)
                    .placeholder(com.mnnit.moticlubs.R.drawable.outline_account_circle_24)
                    .build()
            ),
            contentDescription = "",
            modifier = modifier
                .clip(CircleShape)
                .size(48.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    appViewModel.appScreenMode.value = AppScreenMode.LOGIN
                }
        )
    }
}