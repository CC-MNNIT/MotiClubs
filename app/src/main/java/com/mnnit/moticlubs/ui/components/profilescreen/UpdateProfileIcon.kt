package com.mnnit.moticlubs.ui.components.profilescreen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
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
import com.mnnit.moticlubs.domain.util.compressBitmap
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import java.io.ByteArrayOutputStream

@Composable
fun RowScope.UpdateProfileIcon(
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    loading: MutableState<Boolean>
) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            loading.value = true
            updateClubProfilePicture(context, result.uriContent!!, appViewModel, loading)
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

private fun updateClubProfilePicture(
    context: Context,
    imageUri: Uri,
    appViewModel: AppViewModel,
    loading: MutableState<Boolean>
) {
    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)

    val bitmap = compressBitmap(imageUri, context)
    bitmap ?: return

    val boas = ByteArrayOutputStream()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, boas)
    } else {
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, boas)
    }
    profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
        if (!task.isSuccessful) {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUrl = task.result.toString()
            appViewModel.updateProfilePic(downloadUrl, {
                loading.value = false
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
