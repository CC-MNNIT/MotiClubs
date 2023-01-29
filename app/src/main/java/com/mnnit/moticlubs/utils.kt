package com.mnnit.moticlubs

import android.content.Context
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.*

object Constants {
    const val ADMIN_NAME = "admin_name"
    const val TIME = "time"
    const val MESSAGE = "message"
    const val AVATAR = "avatar"
    const val SHARED_PREFERENCE = "com.mnnit.moticlubs"
    const val POST_URL = "app://moticlubs.mnnit.com"
    const val TOKEN = "token"
    const val EMAIL = "email"
    const val BASE_URL = "https://moti-clubs.vercel.app"
    const val CLUB_NAME = "club_name"
    const val CLUB_ID = "club_id"
    const val CLUB_DESC = "club_desc"
    const val EDIT_MODE = "edit_mode"
    const val POST_ID = "post_id"
    const val CLUB = "club"
}

fun Context.getMkdFormatter() = Markwon.builder(this)
    .usePlugin(StrikethroughPlugin.create())
    .usePlugin(MarkwonInlineParserPlugin.create())
    .usePlugin(LinkifyPlugin.create())
    .usePlugin(TablePlugin.create(this))
    .build()

fun String.getDomainMail(): String = "$this@mnnit.ac.in"

fun Context.setAuthToken(token: String) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putString(Constants.TOKEN, token).apply()

fun Context.getAuthToken(): String =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(Constants.TOKEN, "") ?: ""

fun Context.postRead(clubID: String, postID: String, read: Boolean = false) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putStringSet(clubID, getUnreadPost(clubID).apply {
            if (read) remove(postID) else add(postID)
        }).apply()
}

fun Context.getUnreadPost(clubID: String) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getStringSet(clubID, setOf())
        ?.toMutableSet()
        ?: setOf<String>().toMutableSet()

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
