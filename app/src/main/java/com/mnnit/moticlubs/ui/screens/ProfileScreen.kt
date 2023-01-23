package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import java.io.ByteArrayOutputStream

@Composable
fun ProfileScreen(appViewModel: AppViewModel, onNavigationLogout: () -> Unit) {
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }

    MotiClubsTheme(getColorScheme()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = appViewModel.paddingValues.value.top(),
                    start = maxOf(appViewModel.paddingValues.value.start(), 8.dp),
                    end = maxOf(appViewModel.paddingValues.value.end(), 8.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = appViewModel.paddingValues.value.top())
                    .verticalScroll(scrollState)
                    .wrapContentHeight(Alignment.Top),
            ) {
                ProfileIcon(appViewModel = appViewModel, modifier = Modifier.align(Alignment.CenterHorizontally))

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

            if (showDialog.value) {
                ConfirmationDialog(appViewModel, onNavigationLogout, showDialog)
            }
        }
    }
}

@Composable
fun ProfileIcon(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(
            painter = if (appViewModel.avatar.value.isEmpty()) {
                rememberVectorPainter(image = Icons.Outlined.AccountCircle)
            } else {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(appViewModel.avatar.value)
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
            onClick = { /*TODO*/ },
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

@Composable
fun ConfirmationDialog(
    appViewModel: AppViewModel, onNavigationLogout: () -> Unit,
    showDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val colorScheme = getColorScheme()

    AlertDialog(onDismissRequest = {
        showDialog.value = false
    }, text = {
        Text(text = "Are you sure you want to logout ?", fontSize = 16.sp)
    }, confirmButton = {
        TextButton(onClick = {
            appViewModel.logoutUser(context)
            onNavigationLogout()
        }) {
            Text(text = "Logout", fontSize = 14.sp, color = colorScheme.primary)
        }
    }, dismissButton = {
        TextButton(onClick = { showDialog.value = false }) {
            Text(text = "Cancel", fontSize = 14.sp, color = colorScheme.primary)
        }
    }, icon = {
        Icon(
            painter = rememberVectorPainter(image = Icons.Rounded.Logout),
            contentDescription = "",
            modifier = Modifier.size(36.dp)
        )
    })
}

private fun updateProfilePicture(imageUri: Uri,appViewModel: AppViewModel) {
    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

    val bitmap = compressBitmap(imageUri)
    if (bitmap == null) {
//        Toast.makeText(requireContext(), "Internal bitmap error", Toast.LENGTH_SHORT).show()
        return
    }
    val boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
    profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
//                Log.d(TAG, "not success$it")
                throw it
            }
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
//            Log.d(TAG, "updateProfilePicture: got url")
            val downloadUri = task.result
            API.updateProfilePic(appViewModel.getAuthToken(context = ), {
                Toast.makeText(requireContext(), "Profile pic updated", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "saved on mongodb")
                UserInstance.setAvatar(it.avatar)
                setProfileValues()
            }) { Toast.makeText(requireContext(), "$it: Could not update profile pic", Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(requireContext(), "DB: Could not update profile pic", Toast.LENGTH_SHORT).show()
        }
    }
}
