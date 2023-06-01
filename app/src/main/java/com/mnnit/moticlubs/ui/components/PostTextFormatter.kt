package com.mnnit.moticlubs.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.domain.util.compressBitmap
import com.mnnit.moticlubs.ui.viewmodel.ClubScreenViewModel
import java.io.ByteArrayOutputStream

@Composable
fun PostTextFormatter(viewModel: ClubScreenViewModel) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (uri == null) {
                Toast.makeText(context, "Image not selected", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            uploadPostPic(context, uri, viewModel) { url ->
                val post = viewModel.eventPostMsg.value.text
                val selection = viewModel.eventPostMsg.value.selection
                val urlLink = "\n<img src=\"$url\">\n"
                val msgLink = "\n[image_${viewModel.eventImageReplacerMap.size}]\n"
                viewModel.eventImageReplacerMap[msgLink.replace("\n", "")] = urlLink

                viewModel.eventPostMsg.value = TextFieldValue(
                    post.replaceRange(selection.start, selection.end, msgLink),
                    selection = TextRange(selection.end + msgLink.length, selection.end + msgLink.length)
                )
            }
        } else {
            val exception = result.error
            Toast.makeText(context, "Error ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setCropShape(CropImageView.CropShape.RECTANGLE)
        imageCropLauncher.launch(cropOptions)
    }

    if (viewModel.showLinkDialog.value) {
        InputLinkDialog(
            showDialog = viewModel.showLinkDialog,
            inputLinkName = viewModel.inputLinkName,
            inputLink = viewModel.inputLink,
            postMsg = viewModel.eventPostMsg
        )
    }

    if (viewModel.showGuidanceDialog.value) {
        PostGuidanceDialog(viewModel.showGuidanceDialog)
    }

    AnimatedVisibility(visible = !viewModel.isPreviewMode.value, enter = fadeIn(), exit = fadeOut()) {
        Row(
            modifier = Modifier
                .imePadding()
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { formatMsg(viewModel, "**") }) {
                Icon(Icons.Rounded.FormatBold, contentDescription = "")
            }

            IconButton(onClick = { formatMsg(viewModel, "_") }) {
                Icon(Icons.Rounded.FormatItalic, contentDescription = "")
            }

            IconButton(onClick = { formatMsg(viewModel, "~~") }) {
                Icon(Icons.Rounded.FormatStrikethrough, contentDescription = "")
            }

            IconButton(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Rounded.InsertPhoto, contentDescription = "")
            }

            IconButton(onClick = { viewModel.showLinkDialog.value = true }) {
                Icon(Icons.Rounded.InsertLink, contentDescription = "")
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.showGuidanceDialog.value = true }) {
                Icon(Icons.Outlined.Info, contentDescription = "")
            }
        }
    }
}

private fun formatMsg(viewModel: ClubScreenViewModel, token: String) {
    val str = viewModel.eventPostMsg.value.text
    val tr = viewModel.eventPostMsg.value.selection
    val subStr = str.substring(tr.start, tr.end)
    if (subStr.isEmpty()) return

    val offset = token.length * 2
    viewModel.eventPostMsg.value = TextFieldValue(
        str.replaceRange(tr.start, tr.end, "$token$subStr$token"),
        selection = TextRange(tr.end + offset, tr.end + offset)
    )
}

private fun uploadPostPic(
    context: Context,
    imageUri: Uri,
    viewModel: ClubScreenViewModel,
    onUploaded: (url: String) -> Unit
) {
    viewModel.showProgress.value = true
    viewModel.progressText.value = "Uploading ..."

    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("post_images").child(viewModel.channelModel.channelID.toString())
            .child(System.currentTimeMillis().toString())

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
            viewModel.showProgress.value = false
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            viewModel.showProgress.value = false
            onUploaded(downloadUri.toString())
        } else {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            viewModel.showProgress.value = false
        }
    }
}
