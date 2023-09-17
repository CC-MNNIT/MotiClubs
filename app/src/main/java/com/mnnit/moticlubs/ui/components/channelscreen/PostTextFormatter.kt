package com.mnnit.moticlubs.ui.components.channelscreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatStrikethrough
import androidx.compose.material.icons.rounded.InsertLink
import androidx.compose.material.icons.rounded.InsertPhoto
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
import com.mnnit.moticlubs.domain.util.ImageUploadManager
import com.mnnit.moticlubs.ui.viewmodel.ChannelScreenViewModel

@Composable
fun PostTextFormatter(viewModel: ChannelScreenViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (uri == null) {
                Toast.makeText(context, "Image not selected", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            ImageUploadManager.uploadImageToFirebase(
                context = context,
                imageUri = uri,
                loading = viewModel.showProgress,
                storageRef = Firebase.storage.reference.child("post_images")
                    .child(viewModel.channelModel.channelId.toString())
                    .child(System.currentTimeMillis().toString()),
                onSuccess = { downloadUrl ->
                    viewModel.showProgress.value = false
                    val post = viewModel.eventPostMsg.value.text
                    val selection = viewModel.eventPostMsg.value.selection
                    val urlLink = "\n<img src=\"$downloadUrl\">\n"
                    val msgLink = "\n[image_${viewModel.eventImageReplacerMap.value.size}]\n"
                    viewModel.eventImageReplacerMap.value[msgLink.replace("\n", "")] = urlLink

                    viewModel.eventPostMsg.value = TextFieldValue(
                        post.replaceRange(selection.start, selection.end, msgLink),
                        selection = TextRange(selection.end + msgLink.length, selection.end + msgLink.length),
                    )
                },
            )
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
        PostInputLinkDialog(
            showDialog = viewModel.showLinkDialog,
            inputLinkName = viewModel.inputLinkName,
            inputLink = viewModel.inputLink,
            postMsg = viewModel.eventPostMsg,
        )
    }

    if (viewModel.showGuidanceDialog.value) {
        PostGuidanceDialog(viewModel.showGuidanceDialog)
    }

    AnimatedVisibility(visible = !viewModel.isPreviewMode.value, enter = fadeIn(), exit = fadeOut()) {
        Row(
            modifier = modifier
                .imePadding()
                .padding(top = 8.dp)
                .fillMaxWidth(),
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

private fun formatMsg(viewModel: ChannelScreenViewModel, token: String) {
    val str = viewModel.eventPostMsg.value.text
    val tr = viewModel.eventPostMsg.value.selection
    val subStr = str.substring(tr.start, tr.end)
    if (subStr.isEmpty()) return

    val offset = token.length * 2
    viewModel.eventPostMsg.value = TextFieldValue(
        str.replaceRange(tr.start, tr.end, "$token$subStr$token"),
        selection = TextRange(tr.end + offset, tr.end + offset),
    )
}
