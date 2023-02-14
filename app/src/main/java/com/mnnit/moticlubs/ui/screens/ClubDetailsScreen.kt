@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.mnnit.moticlubs.compressBitmap
import com.mnnit.moticlubs.network.model.UrlResponseModel
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.ClubDetailsScreenViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ClubDetailsScreen(
    appViewModel: AppViewModel,
    viewModel: ClubDetailsScreenViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    viewModel.isAdmin = appViewModel.user.admin.any { m -> m.clubID == viewModel.clubModel.id }

    val refreshState = rememberPullRefreshState(refreshing = viewModel.isFetching, onRefresh = viewModel::fetchUrls)

    val context = LocalContext.current

    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme(2.dp, false)

        Surface(modifier = Modifier.fillMaxWidth()) {
            Scaffold(modifier = Modifier.fillMaxWidth()) {
                if (viewModel.showProgressDialog.value) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showSocialLinkDialog.value) {
                    InputSocialLinkDialog(
                        showDialog = viewModel.showOtherLinkDialog,
                        socialLinksLiveList = viewModel.socialLinksLiveList,
                        otherLinksLiveList = viewModel.otherLinksLiveList
                    ) { list ->
                        handleUrls(viewModel, context, list)
                    }
                }

                if (viewModel.showOtherLinkDialog.value) {
                    InputOtherLinkDialog(
                        showDialog = viewModel.showOtherLinkDialog,
                        showColorPaletteDialog = viewModel.showColorPaletteDialog,
                        otherLinksLiveList = viewModel.otherLinksLiveList,
                        otherLinkIdx = viewModel.otherLinkIdx,
                        socialLinksLiveList = viewModel.socialLinksLiveList
                    ) { list ->
                        handleUrls(viewModel, context, list)
                    }
                }

                if (viewModel.showColorPaletteDialog.value) {
                    ColorPaletteDialog(
                        otherLinkComposeModel = viewModel.otherLinksLiveList[viewModel.otherLinkIdx.value],
                        viewModel.showColorPaletteDialog
                    )
                }

                Column(
                    modifier = Modifier
                        .pullRefresh(state = refreshState)
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = viewModel.isFetching || refreshState.progress.dp.value > 0.5f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.surfaceColorAtElevation(2.dp))
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            strokeCap = StrokeCap.Round
                        )
                    }

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
                            Text(
                                modifier = Modifier.padding(),
                                text = "${viewModel.clubModel.subscribers} Members",
                                fontSize = 12.sp
                            )

                            val socials = viewModel.socialLinks.filter { f -> f.name.isNotEmpty() }
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
                                                        this.clubID = viewModel.clubModel.id
                                                    }
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        Text(viewModel.clubModel.description, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubProfilePic(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.progressMsg = "Uploading ..."
            viewModel.showProgressDialog.value = true
            updateClubProfilePicture(context, result.uriContent!!, viewModel, viewModel.showProgressDialog)
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

    Row(modifier = modifier) {
        ProfilePicture(
            modifier = Modifier.padding(start = if (viewModel.isAdmin) 46.dp else 0.dp),
            url = viewModel.clubModel.avatar,
            size = 156.dp
        )

        if (viewModel.isAdmin) {
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
}

private fun updateClubProfilePicture(
    context: Context,
    imageUri: Uri,
    viewModel: ClubDetailsScreenViewModel,
    loading: MutableState<Boolean>
) {
    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("profile_images").child(viewModel.clubModel.id.toString())

    val bitmap = compressBitmap(imageUri, context)
    bitmap ?: return

    val boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
    profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
        if (!task.isSuccessful) {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUrl = task.result.toString()
            viewModel.updateProfilePic(downloadUrl, {
                loading.value = false
                viewModel.clubModel.avatar = downloadUrl
            }) {
                loading.value = false
                Toast.makeText(context, "Error setting profile picture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
    }
}

private fun handleUrls(viewModel: ClubDetailsScreenViewModel, context: Context, list: List<UrlResponseModel>) {
    viewModel.progressMsg = "Updating"
    viewModel.showProgressDialog.value = true
    viewModel.showSocialLinkDialog.value = false
    viewModel.showOtherLinkDialog.value = false
    viewModel.pushUrls(list, {
        viewModel.fetchUrls()

        viewModel.showProgressDialog.value = false
        Toast.makeText(context, "Links updated", Toast.LENGTH_SHORT).show()
    }) { code ->
        viewModel.showProgressDialog.value = false
        Toast.makeText(context, "$code: Error updating links", Toast.LENGTH_SHORT).show()
    }
}
