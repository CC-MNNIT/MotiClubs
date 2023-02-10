package com.mnnit.moticlubs.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.compressBitmap
import com.mnnit.moticlubs.ui.screens.ClubScreenViewModel
import com.mnnit.moticlubs.ui.screens.InputLinkDialog
import java.io.ByteArrayOutputStream

@Composable
fun TextFormatter(viewModel: ClubScreenViewModel) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = {
        if (it == null) {
            Toast.makeText(context, "Image not selected", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        uploadPostPic(context, it, viewModel) { url ->
            val post = viewModel.postMsg.value.text
            val selection = viewModel.postMsg.value.selection
            val urlLink = "\n![post_img]($url)\n"
            viewModel.postMsg.value = TextFieldValue(
                post.replaceRange(selection.start, selection.end, urlLink),
                selection = TextRange(selection.end + urlLink.length, selection.end + urlLink.length)
            )
        }
    })

    if (viewModel.showLinkDialog.value) {
        InputLinkDialog(viewModel = viewModel)
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
        }
    }
}

private fun formatMsg(viewModel: ClubScreenViewModel, token: String) {
    val str = viewModel.postMsg.value.text
    val tr = viewModel.postMsg.value.selection
    val subStr = str.substring(tr.start, tr.end)
    if (subStr.isEmpty()) return

    val offset = token.length * 2
    viewModel.postMsg.value = TextFieldValue(
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
        storageRef.child("post_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(System.currentTimeMillis().toString())

    val bitmap = compressBitmap(imageUri, context)
    bitmap ?: return

    val boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
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
