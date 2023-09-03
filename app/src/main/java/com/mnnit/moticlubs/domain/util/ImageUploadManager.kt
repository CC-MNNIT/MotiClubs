package com.mnnit.moticlubs.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

object ImageUploadManager {

    fun uploadImageToFirebase(
        context: Context,
        imageUri: Uri,
        loading: MutableState<Boolean>,
        storageRef: StorageReference,
        onSuccess: (downloadUrl: String) -> Unit
    ) {
        if (!context.connectionAvailable()) {
            Toast.makeText(context, "You're Offline", Toast.LENGTH_SHORT).show()
            loading.value = false
            return
        }

        val bitmap = compressBitmap(imageUri, context)
        bitmap ?: return

        val boas = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, boas)
        } else {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, boas)
        }
        storageRef.putBytes(boas.toByteArray()).continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                loading.value = false
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                onSuccess(downloadUrl)
            } else {
                Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                loading.value = false
            }
        }
    }

    private fun compressBitmap(uri: Uri, context: Context): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val ins = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(ins, null, options)
        ins?.close()

        var scale = 1
        while (options.outWidth / scale / 2 >= 200 && options.outHeight / scale / 2 >= 200) {
            scale *= 2
        }

        val finalOptions = BitmapFactory.Options()
        finalOptions.inSampleSize = scale

        val inputStream = context.contentResolver.openInputStream(uri)
        val out = BitmapFactory.decodeStream(inputStream, null, finalOptions)
        inputStream?.close()
        return out
    }
}
