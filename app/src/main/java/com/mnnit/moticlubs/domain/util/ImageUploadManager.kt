package com.mnnit.moticlubs.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

object ImageUploadManager {

    fun prepareImage(
        context: Context,
        imageUri: Uri,
        loading: PublishedState<Boolean>,
        onSuccess: (file: File) -> Unit,
    ) {
        if (!context.connectionAvailable()) {
            Toast.makeText(context, "You're Offline", Toast.LENGTH_SHORT).show()
            loading.value = false
            return
        }

        val bitmap = compressBitmap(imageUri, context)
        bitmap ?: return

        try {
            val file = File(context.cacheDir, "tmp.webp")
            if (!file.exists() && !file.createNewFile()) {
                throw Exception("Unable to create temp webp file")
            }

            val fos = FileOutputStream(file)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, fos)
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos)
            }
            bitmap.recycle()
            fos.close()

            onSuccess(file)
        } catch (e: Exception) {
            Toast.makeText(context, "Error ${e.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
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
