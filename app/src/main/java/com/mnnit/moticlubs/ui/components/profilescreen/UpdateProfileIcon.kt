package com.mnnit.moticlubs.ui.components.profilescreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.domain.util.ImageUploadManager
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun UpdateProfileIcon(
    appViewModel: HomeScreenViewModel,
    loading: PublishedState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            loading.value = true

            ImageUploadManager.uploadImageToFirebase(
                context = context,
                imageUri = result.uriContent!!,
                loading = loading,
                storageRef = Firebase.storage.reference
                    .child("profile_images")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid),
                onSuccess = { downloadUrl ->
                    appViewModel.updateProfilePic(downloadUrl, {
                        loading.value = false
                    }) {
                        loading.value = false
                        Toast.makeText(context, "Error setting profile picture", Toast.LENGTH_SHORT).show()
                    }
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

    IconButton(
        onClick = { launcher.launch("image/*") },
        modifier = modifier.size(42.dp),
    ) {
        Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
    }
}
