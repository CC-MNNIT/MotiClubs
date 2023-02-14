package com.mnnit.moticlubs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import com.mnnit.moticlubs.network.model.ClubModel
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.*

object Constants {
    const val USER_ID = "userID"
    const val SHARED_PREFERENCE = "com.mnnit.moticlubs"
    const val POST_URL = "app://moticlubs.mnnit.com"
    const val TOKEN = "token"

    const val BASE_URL = "https://api-moticlubs.up.railway.app/"
}

fun Context.getMkdFormatter() = Markwon.builder(this)
    .usePlugin(StrikethroughPlugin.create())
    .usePlugin(MarkwonInlineParserPlugin.create())
    .usePlugin(LinkifyPlugin.create())
    .usePlugin(TablePlugin.create(this))
    .build()

fun String.getDomainMail(): String = "$this@mnnit.ac.in"

fun Context.setUserID(userID: Int) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putInt(Constants.USER_ID, userID).apply()
}

fun Context.getUserID(): Int =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.USER_ID, -1)

fun Context.setAuthToken(token: String) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putString(Constants.TOKEN, token).apply()

fun Context.getAuthToken(): String =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(Constants.TOKEN, "") ?: ""

fun Context.clubHasUnreadPost(clubModel: ClubModel): Boolean {
    var has = false
    for (i in clubModel.channels.indices) {
        if (getUnreadPost(clubModel.channels[i].channelID).isNotEmpty()) {
            has = true
            break
        }
    }
    return has
}

fun Context.postRead(channelID: Int, postID: Int, read: Boolean = false) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putStringSet("ch$channelID", getUnreadPost(channelID).apply {
            if (read) remove(postID.toString()) else add(postID.toString())
        }).apply()
}

fun Context.getUnreadPost(channelID: Int) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .getStringSet("ch$channelID", setOf())
        ?.toMutableSet() ?: setOf<String>().toMutableSet()

fun Context.getExpandedChannel(clubID: Int): Boolean =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getBoolean("cl$clubID", false)

fun Context.setExpandedChannel(clubID: Int, expanded: Boolean) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .edit().putBoolean("cl$clubID", expanded).apply()
}

private val mMonthsList: List<String> = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun Long.toTimeString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this

    val hour = calendar.get(Calendar.HOUR)
    val min = calendar.get(Calendar.MINUTE)
    val amPm = calendar.get(Calendar.AM_PM)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)

    return "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"} " + "${if (amPm == Calendar.AM) "AM" else "PM"}, $day ${mMonthsList[month]}"
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

fun Context.connectionAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nwCap = connectivityManager.activeNetwork
    val activeNw = connectivityManager.getNetworkCapabilities(nwCap) ?: return false
    return when {
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}
