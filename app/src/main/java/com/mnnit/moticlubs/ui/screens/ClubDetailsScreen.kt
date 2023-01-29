package com.mnnit.moticlubs.ui.screens

import android.util.Log
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme



@Composable
fun ClubDetailsScreen(appViewModel: AppViewModel) {

    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Scaffold(
                modifier = Modifier,
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Edit", fontSize = 15.sp, textAlign = TextAlign.Center) },
                        icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = "") },
                        onClick = { },
                        shape = RoundedCornerShape(24.dp),
                    )
                },
                floatingActionButtonPosition = FabPosition.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClubProfilePic(
                        clubModel = appViewModel.clubModel.value,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    ClubInfo(
                        clubModel = appViewModel.clubModel.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClubProfilePic(clubModel: ClubModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
//            loading.value = true
//            updateProfilePicture(context, result.uriContent!!, appViewModel, loading)
        } else {
            val exception = result.error
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(1, 1)
        imageCropLauncher.launch(cropOptions)
    }

    Row(modifier = modifier) {
        Image(
            painter = if (clubModel.avatar.isEmpty() || !clubModel.avatar.matches(Patterns.WEB_URL.toRegex())) {
                rememberVectorPainter(image = Icons.Outlined.AccountCircle)
            } else {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(clubModel.avatar)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(Constants.AVATAR)
                        .placeholder(R.drawable.outline_account_circle_24)
                        .build()
                )
            }, contentDescription = "",
            modifier = modifier
                .padding(start = 46.dp)
                .clip(CircleShape)
                .size(156.dp)
        )

        IconButton(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier
                .align(Alignment.Bottom)
                .border(1.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
        }
    }
}

@Composable
fun ClubInfo(clubModel: ClubModel, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Text(clubModel.name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Text(clubModel.description, fontSize = 14.sp)
    }
}

//@Composable
//private fun UserInfoFields(clubModel: ClubModel, containerHeight: Dp) {
//    Column {
//        Spacer(modifier = Modifier.height(8.dp))
//
//        NameAndDescription(clubModel)
//
//        ProfileProperty(stringResource(R.string.club_name), clubModel.name)
//
//        ProfileProperty(stringResource(R.string.members_cnt), clubModel.description)
//
//        ProfileProperty(stringResource(R.string.twitter), "https://www.github.com/hackeramitkumar", isLink = true)
//
//        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
//        // in order to always leave some content at the top.
//        Spacer(Modifier.height((containerHeight - 320.dp).coerceAtLeast(0.dp)))
//    }
//}
//
//@Composable
//private fun NameAndDescription(
//    clubModel: ClubModel
//) {
//    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//        Name(
//            clubModel,
//            modifier = Modifier.height(32.dp)
//        )
//        Position(
//            clubModel,
//            modifier = Modifier.padding(bottom = 10.dp)
//        )
//    }
//}
//
//@Composable
//private fun Name(clubModel: ClubModel, modifier: Modifier = Modifier) {
//    Text(
//        text = clubModel.name,
//        modifier = modifier,
//        style = MaterialTheme.typography.headlineSmall
//    )
//}
//
//@Composable
//private fun Position(clubModel: ClubModel, modifier: Modifier = Modifier) {
//    Text(
//        text = clubModel.description,
//        modifier = modifier,
//        style = MaterialTheme.typography.bodyLarge,
//        color = MaterialTheme.colorScheme.onSurfaceVariant
//    )
//}
//
//@Composable
//private fun ProfileHeader(
//    scrollState: ScrollState,
//    clubModel: ClubModel
//) {
//    Image(
//        modifier = Modifier
//            .clip(CircleShape)
//            .size(256.dp),
//        painter = if (clubModel.avatar.isEmpty()) {
//            rememberAsyncImagePainter(model = R.drawable.someone_else)
//        } else {
//            rememberAsyncImagePainter(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(clubModel.avatar)
//                    .diskCachePolicy(CachePolicy.ENABLED)
//                    .diskCacheKey(Constants.AVATAR)
//                    .placeholder(R.drawable.outline_account_circle_24)
//                    .build()
//            )
//        },
//        contentScale = ContentScale.Crop,
//        contentDescription = null,
//    )
//}
//
//@Composable
//fun ProfileProperty(label: String, value: String, isLink: Boolean = false) {
//    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
//        Divider()
//        Text(
//            text = label,
//            modifier = Modifier.padding(top = 10.dp),
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        val style = if (isLink) {
//            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//        } else {
//            MaterialTheme.typography.bodyLarge
//        }
//        Text(
//            text = value,
//            modifier = Modifier.height(24.dp),
//            style = style
//        )
//    }
//}
//
//@Composable
//fun ProfileError() {
//    Text("Some error occured")
//}
