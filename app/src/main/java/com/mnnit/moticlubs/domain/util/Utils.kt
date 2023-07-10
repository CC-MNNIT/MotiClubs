package com.mnnit.moticlubs.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.Calendar

object Constants {
    const val USER_ID = "userID"
    const val SHARED_PREFERENCE = "com.mnnit.moticlubs"
    const val APP_SCHEME_URL = "app://moticlubs.mnnit.com"
    const val TOKEN = "token"

//    const val BASE_URL = "https://api-moticlubs.up.railway.app/"
//    const val BASE_URL = "http://172.18.19.238:3000/api/"
//    const val BASE_URL = "http://192.168.1.114:3000/api/"
//    const val BASE_URL = "http://192.168.1.114:8002/api/"
    const val BASE_URL = "https://sac.mnnit.ac.in/api/"

    // either "" or "/"
    const val URL_PREFIX = ""
}

fun Context.getMkdFormatter() = Markwon.builder(this)
    .usePlugin(StrikethroughPlugin.create())
    .usePlugin(MarkwonInlineParserPlugin.create())
    .usePlugin(LinkifyPlugin.create())
    .usePlugin(TablePlugin.create(this))
    .build()

fun String.getDomainMail(): String = "$this@mnnit.ac.in"

private val mMonthsList: List<String> = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun Long.toTimeString(): String {
    val minuteMilli = 60 * 1000
    var time = this
    if (time < 1000000000000L) time *= 1000

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) return "Future"

    val diff = now - time
    return when {
        diff < minuteMilli -> "Just now"
        diff < 2 * minuteMilli -> "A minute ago"
        diff < 50 * minuteMilli -> "${diff / minuteMilli} minutes ago"
        else -> {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this

            val hour = calendar.get(Calendar.HOUR)
            val min = calendar.get(Calendar.MINUTE)
            val amPm = calendar.get(Calendar.AM_PM)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)

            "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"} " +
                    "${if (amPm == Calendar.AM) "AM" else "PM"}, $day ${mMonthsList[month]}"
        }
    }
}

fun compressBitmap(uri: Uri, context: Context): Bitmap? {
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
