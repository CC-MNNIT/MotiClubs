@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.ImageUploadManager
import com.mnnit.moticlubs.domain.util.Links
import com.mnnit.moticlubs.domain.util.SocialLinkComposeModel
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.components.clubdetailscreen.DescriptionComponent
import com.mnnit.moticlubs.ui.components.clubdetailscreen.InputOtherLinkDialog
import com.mnnit.moticlubs.ui.components.clubdetailscreen.InputSocialLinkDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ClubDetailsScreenViewModel

@Composable
fun ClubDetailsScreen(
    onNavigateBackPressed: () -> Unit,
    viewModel: ClubDetailsScreenViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetching,
        onRefresh = viewModel::refresh
    )
    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme(2.dp, false)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                if (viewModel.showProgressDialog.value) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showSocialLinkDialog.value) {
                    InputSocialLinkDialog(
                        showDialog = viewModel.showSocialLinkDialog,
                        socialLinksLiveList = viewModel.socialLinksLiveList,
                        otherLinksLiveList = viewModel.otherLinksLiveList
                    ) { list -> viewModel.pushUrls(list) }
                }

                if (viewModel.showOtherLinkDialog.value) {
                    InputOtherLinkDialog(
                        showDialog = viewModel.showOtherLinkDialog,
                        showColorPaletteDialog = viewModel.showColorPaletteDialog,
                        otherLinksLiveList = viewModel.otherLinksLiveList,
                        otherLinkIdx = viewModel.otherLinkIdx,
                        socialLinksLiveList = viewModel.socialLinksLiveList
                    ) { list -> viewModel.pushUrls(list) }
                }

                if (viewModel.showColorPaletteDialog.value) {
                    ColorPaletteDialog(
                        otherLinkComposeModel = viewModel.otherLinksLiveList[viewModel.otherLinkIdx.intValue],
                        viewModel.showColorPaletteDialog
                    )
                }

                Column(
                    modifier = Modifier
                        .pullRefresh(state = refreshState)
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(scrollState)
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    PullDownProgressIndicator(
                        modifier = Modifier.background(colorScheme.surfaceColorAtElevation(2.dp)),
                        visible = viewModel.isFetching,
                        refreshState = refreshState
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .pullRefresh(state = refreshState)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            ClubProfilePic(
                                onNavigateBackPressed,
                                viewModel = viewModel,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = viewModel.clubModel.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                modifier = Modifier.padding(top = 0.dp),
                                text = viewModel.clubModel.summary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            val socials = viewModel.socialLinks.filter { f -> f.name.isTrimmedNotEmpty() }
                            if (socials.isNotEmpty() || viewModel.isAdmin) {
                                Links(
                                    isAdmin = viewModel.isAdmin,
                                    "Socials",
                                    socials,
                                    onClick = {
                                        for (i in viewModel.socialLinks.indices) {
                                            viewModel.socialLinksLiveList[i] =
                                                viewModel.socialLinks[i].mapToSocialLinkModel()
                                                    .apply {
                                                        this.urlName = SocialLinkComposeModel.socialLinkNames[i]
                                                        this.clubID = viewModel.clubModel.clubId
                                                    }
                                            Log.d("TAG", "ClubDetailsScreen: ${viewModel.socialLinksLiveList[i]}")
                                        }

                                        viewModel.showSocialLinkDialog.value = true
                                    }
                                )
                            }
                            if (viewModel.otherLinks.isNotEmpty() || viewModel.isAdmin) {
                                Links(
                                    isAdmin = viewModel.isAdmin,
                                    "Others",
                                    viewModel.otherLinks,
                                    onClick = {
                                        viewModel.otherLinksLiveList.clear()
                                        viewModel.otherLinksLiveList.addAll(
                                            viewModel.otherLinks.map { m -> m.mapToOtherLinkModel() }
                                        )
                                        viewModel.showOtherLinkDialog.value = true
                                    }
                                )
                            }
                        }
                    }

                    DescriptionComponent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun ClubProfilePic(
    onNavigateBackPressed: () -> Unit,
    viewModel: ClubDetailsScreenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.progressMsg = "Uploading ..."
            viewModel.showProgressDialog.value = true

            ImageUploadManager.uploadImageToFirebase(
                context = context,
                imageUri = result.uriContent!!,
                viewModel.showProgressDialog,
                storageRef = Firebase.storage.reference
                    .child("profile_images")
                    .child(viewModel.clubModel.clubId.toString()),
                onSuccess = { downloadUrl ->
                    viewModel.updateClub(url = downloadUrl, onResponse = {
                        viewModel.showProgressDialog.value = false
                        viewModel.clubModel = viewModel.clubModel.copy(avatar = downloadUrl)
                    }, onFailure = {
                        viewModel.showProgressDialog.value = false
                        Toast.makeText(context, "Error setting profile picture", Toast.LENGTH_SHORT).show()
                    })
                }
            )
        } else {
            val exception = result.error
            Toast.makeText(context, "Error ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(1, 1)
        imageCropLauncher.launch(cropOptions)
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(42.dp),
                onClick = { onNavigateBackPressed() }
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.isAdmin) {
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(42.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.AddAPhoto, contentDescription = "")
                }
            }
        }

        ProfilePicture(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            userModel = User().copy(avatar = viewModel.clubModel.avatar),
            size = 156.dp,
            onClick = {}
        )
    }
}
