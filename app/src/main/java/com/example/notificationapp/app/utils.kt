package com.example.notificationapp.app

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
    const val SHARED_PREFERENCE = "com.example.notificationapp"
    const val TOKEN = "token"
    const val EMAIL = "email"
    const val BASE_URL = "https://notification-app-mnnit.vercel.app"
    const val CLUB_NAME = "club_name"
    const val CLUB_ID = "club_id"
    const val CLUB_DESC = "club_desc"
}

fun String.isNotValidDomain(): Boolean = !"^([^\\s]+)@mnnit.ac.in$".toRegex().containsMatchIn(this)

private val mMonthsList: List<String> = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
    "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun Long.toTimeString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this

    val hour = calendar.get(Calendar.HOUR)
    val min = calendar.get(Calendar.MINUTE)
    val amPm = calendar.get(Calendar.AM_PM)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)

    return "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"} " +
            "${if (amPm == Calendar.AM) "AM" else "PM"}, $day ${mMonthsList[month]}"
}

fun Context.getMkdFormatter() = Markwon.builder(this)
    .usePlugin(StrikethroughPlugin.create())
    .usePlugin(MarkwonInlineParserPlugin.create())
    .usePlugin(LinkifyPlugin.create())
    .usePlugin(TablePlugin.create(this))
    .build()
