package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.ClubDTO
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.api.Repository.updateClub
import com.mnnit.moticlubs.compressBitmap
import com.mnnit.moticlubs.getAuthToken
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor() : ViewModel() {
    val initialClubModel = mutableStateOf(ClubModel("", "", "", "", listOf(), listOf()))
    val description = mutableStateOf("")
    val avatar_url = mutableStateOf("")
    val showLinkDialog = mutableStateOf(false)
    var socialMediaUrls = mutableListOf("", "", "", "", "", "")
    val isLoading = mutableStateOf(false)
    val faceBookUrl = mutableStateOf("")
    val instagramUrl = mutableStateOf("")
    val linkedInUrl = mutableStateOf("")
    val websiteUrl = mutableStateOf("")
    val githubUrl = mutableStateOf("")
    val socialMediaUrlUpdated = mutableStateOf(false)
    var isEditButtonEnabled = false
        get() = !isLoading.value
                && ((initialClubModel.value.avatar != avatar_url.value) || (initialClubModel.value.description != description.value) || socialMediaUrlUpdated.value)
}

@Composable
fun ClubDetailsScreen(appViewModel: AppViewModel, viewModel: ClubDetailsScreenViewModel = hiltViewModel()) {
    viewModel.initialClubModel.value = appViewModel.clubModel.value
    viewModel.description.value = appViewModel.clubModel.value.description
    viewModel.avatar_url.value = appViewModel.clubModel.value.avatar
    viewModel.socialMediaUrls = appViewModel.clubModel.value.socialUrls.toMutableList()
    viewModel.faceBookUrl.value = appViewModel.clubModel.value.socialUrls[0]
    viewModel.instagramUrl.value = appViewModel.clubModel.value.socialUrls[1]
    viewModel.linkedInUrl.value = appViewModel.clubModel.value.socialUrls[2]
    viewModel.websiteUrl.value = appViewModel.clubModel.value.socialUrls[3]
    viewModel.githubUrl.value = appViewModel.clubModel.value.socialUrls[4]
    val isAdmin = appViewModel.clubModel.value.admins.contains(appViewModel.email.value)
    val context = LocalContext.current

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
                    if (isAdmin)
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Edit", fontSize = 15.sp, textAlign = TextAlign.Center) },
                            icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = "") },
                            onClick = {
                                updateClubModel(
                                    appViewModel, viewModel,
                                    ClubDTO(
                                        viewModel.description.value,
                                        viewModel.avatar_url.value,
                                        viewModel.socialMediaUrls
                                    ), context
                                )
                            },
                            expanded = !viewModel.isEditButtonEnabled,
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
                    if (viewModel.isLoading.value) {
                        ProgressDialog(progressMsg = "Uploading...")
                    }
                    ClubProfilePic(
                        viewModel = viewModel,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        isAdmin = isAdmin
                    )
                    ClubInfo(
                        viewModel = viewModel,
                        appViewModel = appViewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp),
                        context = context,
                        isAdmin = isAdmin
                    )
                }
            }
        }
    }
}

@Composable
fun ClubProfilePic(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier, isAdmin: Boolean) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.isLoading.value = true
            val storageRef = Firebase.storage.reference
            val profilePicRef =
                storageRef.child("club_profile_images").child(viewModel.initialClubModel.value.id)
                    .child(viewModel.initialClubModel.value.id)

            val bitmap = compressBitmap(result.uriContent!!, context)

            val boas = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, boas)
            profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    viewModel.isLoading.value = false
                }
                profilePicRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.avatar_url.value = task.result.toString()
                    viewModel.isLoading.value = false
                } else {
                    Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    viewModel.isLoading.value = false
                }
            }
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
            painter = if (viewModel.avatar_url.value.isEmpty() || !viewModel.avatar_url.value.matches(
                    Patterns.WEB_URL.toRegex()
                )
            ) {
                rememberVectorPainter(image = Icons.Outlined.AccountCircle)
            } else {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(viewModel.avatar_url.value)
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
        if (isAdmin)
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
fun ClubInfo(viewModel: ClubDetailsScreenViewModel,appViewModel: AppViewModel, modifier: Modifier = Modifier, context: Context, isAdmin: Boolean) {
    if (viewModel.showLinkDialog.value) {
        InputSocialLinkDialog(viewModel = viewModel)
    }

    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (viewModel.socialMediaUrls[0].isNotEmpty())
                IconButton(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(viewModel.socialMediaUrls[0])
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook),
                        contentDescription = "",
                        tint = Color.Blue
                    )
                }

            if (viewModel.socialMediaUrls[1].isNotEmpty())
                IconButton(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(viewModel.socialMediaUrls[1])
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Chat),
                        contentDescription = "",
                        tint = Color.Red
                    )
                }

            if (viewModel.socialMediaUrls[2].isNotEmpty())
                IconButton(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(viewModel.socialMediaUrls[2])
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Webhook), contentDescription = ""
                    )
                }

            if (viewModel.socialMediaUrls[3].isNotEmpty())
                IconButton(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(viewModel.socialMediaUrls[3])
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook),
                        contentDescription = "",
                        tint = getColorScheme().primary
                    )
                }

            if (viewModel.socialMediaUrls[4].isNotEmpty())
                IconButton(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(viewModel.socialMediaUrls[4])
                        )
                        context.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook), contentDescription = ""
                    )
                }

            if (isAdmin)
                IconButton(
                    onClick = {
                        viewModel.showLinkDialog.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Add), contentDescription = ""
                    )
                }

        }

        Text(viewModel.initialClubModel.value.name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)

        Text("Members: " + appViewModel.subscriberCount.value, fontSize = 15.sp, fontWeight = FontWeight.Normal)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(top=15.dp),
            value = viewModel.description.value,
            onValueChange = { viewModel.description.value = it },
            shape = RoundedCornerShape(24.dp),
            label = { Text(text = "Description", fontSize = 14.sp) },
            enabled = isAdmin,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLeadingIconColor = contentColorFor(backgroundColor = colorScheme.background)
            )
        )

    }
}

@Composable
fun InputSocialLinkDialog(viewModel: ClubDetailsScreenViewModel) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showLinkDialog.value = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Social-Media URLs",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.faceBookUrl.value,
                    onValueChange = { viewModel.faceBookUrl.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Facebook URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.instagramUrl.value,
                    onValueChange = { viewModel.instagramUrl.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Instagram URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.linkedInUrl.value,
                    onValueChange = { viewModel.linkedInUrl.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "LinkedIn URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.websiteUrl.value,
                    onValueChange = { viewModel.websiteUrl.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Website URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.githubUrl.value,
                    onValueChange = { viewModel.githubUrl.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Github URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        viewModel.socialMediaUrls = mutableListOf(
                            viewModel.faceBookUrl.value,
                            viewModel.instagramUrl.value,
                            viewModel.linkedInUrl.value,
                            viewModel.websiteUrl.value,
                            viewModel.githubUrl.value
                        )
                        if (viewModel.initialClubModel.value.socialUrls.toString() != viewModel.socialMediaUrls.toString()) {
                            viewModel.socialMediaUrlUpdated.value = true
                        }
                        viewModel.showLinkDialog.value = false
                    },
                    enabled = true
                ) {
                    Text(text = "Add Links", fontSize = 14.sp)
                }
            }
        }
    }
}

fun updateClubModel(
    appViewModel: AppViewModel,
    viewModel: ClubDetailsScreenViewModel,
    clubDTO: ClubDTO,
    context: Context
) {
    viewModel.updateClub(context.getAuthToken(), appViewModel.clubModel.value.id, clubDTO = clubDTO, {
        viewModel.socialMediaUrlUpdated.value = false
        viewModel.isEditButtonEnabled = false
        viewModel.isLoading.value = false
        appViewModel.clubModel.value.socialUrls = clubDTO.socialUrls
        appViewModel.clubModel.value.description = clubDTO.description
        appViewModel.clubModel.value.avatar = clubDTO.avatar
        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
    }) { Toast.makeText(context, "$it: Error updating club", Toast.LENGTH_SHORT).show() }

}
