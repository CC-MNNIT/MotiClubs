package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.api.Repository.updateProfilePic
import com.mnnit.moticlubs.compressBitmap
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import java.io.ByteArrayOutputStream

@Composable
fun ProfileScreen(appViewModel: AppViewModel, onNavigationLogout: () -> Unit) {
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    MotiClubsTheme(getColorScheme()) {
        SetNavBarsTheme()
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .verticalScroll(scrollState)
                    .wrapContentHeight(Alignment.Top),
            ) {
                ProfileIcon(
                    appViewModel = appViewModel,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    loading
                )
                UserInfo(appViewModel = appViewModel, modifier = Modifier.padding(top = 56.dp))

                Button(
                    onClick = {
                        showDialog.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Icon(painter = rememberVectorPainter(image = Icons.Rounded.Logout), contentDescription = "")
                    Text(text = "Logout", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
            if (loading.value) {
                ProgressDialog(progressMsg = "Uploading ...")
            }
            if (showDialog.value) {
                ConfirmationDialog(
                    showDialog = showDialog,
                    message = "Are you sure you want to logout ?",
                    positiveBtnText = "Logout",
                    imageVector = Icons.Rounded.Logout,
                    onPositive = {
                        appViewModel.logoutUser(context)
                        onNavigationLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileIcon(appViewModel: AppViewModel, modifier: Modifier = Modifier, loading: MutableState<Boolean>) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            loading.value = true
            updateProfilePicture(context, result.uriContent!!, appViewModel, loading)
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
        ProfilePicture(modifier = modifier.padding(start = 46.dp), url = appViewModel.avatar.value, size = 156.dp)

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .align(Alignment.Bottom)
                .border(1.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
        }
    }
}

@Composable
fun UserInfo(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    val colorScheme = getColorScheme()

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = appViewModel.name.value,
        onValueChange = { },
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "Name") },
        enabled = false,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = appViewModel.email.value.replace("@mnnit.ac.in", ""),
        onValueChange = { },
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "G-Suite ID") },
        enabled = false,
        trailingIcon = {
            Text(
                text = "@mnnit.ac.in",
                modifier = Modifier.padding(end = 16.dp),
                fontWeight = FontWeight.SemiBold
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledTrailingIconColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )

    Row(modifier = Modifier.padding(top = 8.dp)) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 8.dp),
            value = appViewModel.regNo.value,
            onValueChange = {},
            shape = RoundedCornerShape(24.dp),
            label = { Text(text = "Reg No") },
            enabled = false,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
            )
        )

        OutlinedTextField(
            value = appViewModel.course.value,
            onValueChange = { },
            readOnly = true,
            label = { Text(text = "Course") },
            trailingIcon = {
            },
            shape = RoundedCornerShape(24.dp),
            enabled = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
            )
        )
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = appViewModel.phoneNumber.value,
        onValueChange = {},
        shape = RoundedCornerShape(24.dp),
        label = { Text(text = "Phone No") },
        enabled = false,
        singleLine = true,
        leadingIcon = {
            Text(
                text = "+ 91",
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxHeight(),
                textAlign = TextAlign.Center
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
            disabledLeadingIconColor = contentColorFor(backgroundColor = colorScheme.background)
        )
    )
}

private fun updateProfilePicture(
    context: Context,
    imageUri: Uri,
    appViewModel: AppViewModel,
    loading: MutableState<Boolean>
) {
    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

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
            val downloadUri = task.result
            appViewModel.updateProfilePic(context, downloadUri.toString(), {
                appViewModel.avatar.value = it.avatar
                loading.value = false
            }) {}
        } else {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
    }
}
