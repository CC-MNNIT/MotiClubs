package com.mnnit.moticlubs.domain.util

import android.content.Context
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toOffset
import androidx.lifecycle.SavedStateHandle
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.Calendar
import kotlin.math.min
import me.saket.telephoto.zoomable.ZoomableContentLocation

object Constants {
    val EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@mnnit.ac.in$".toRegex()
    val REG_NO_REGEX = "(?<=\\.)(.*?)(?=@)".toRegex()

    const val USER_ID = "userID"
    const val SHARED_PREFERENCE = "com.mnnit.moticlubs"
    const val APP_SCHEME_URL = "app://moticlubs.mnnit.com"
    const val TOKEN = "token"

    const val AUTHORIZATION_HEADER = "Authorization"
    const val STAMP_HEADER = "X-Stamp-Value"

    const val POST_BROADCAST_ACTION = "$SHARED_PREFERENCE.post"
    const val REPLY_BROADCAST_ACTION = "$SHARED_PREFERENCE.reply"

    const val BASE_URL = "https://sac.mnnit.ac.in/moticlubs/"
    private const val URL_PREFIX = "api/v1"
    const val CHANNEL_ROUTE = "$URL_PREFIX/channel"
    const val CLUB_ROUTE = "$URL_PREFIX/clubs"
    const val POST_ROUTE = "$URL_PREFIX/posts"
    const val REPLY_ROUTE = "$URL_PREFIX/reply"
    const val URL_ROUTE = "$URL_PREFIX/url"
    const val USER_ROUTE = "$URL_PREFIX/user"
    const val VIEW_ROUTE = "$URL_PREFIX/views"

    const val USER_ID_CLAIM = "userId"
    const val CLUB_ID_CLAIM = "clubId"
    const val CHANNEL_ID_CLAIM = "channelId"
    const val POST_ID_CLAIM = "postId"
    const val REPLY_ID_CLAIM = "replyId"

    const val INPUT_USER_CONTACT_SIZE = 256
    const val INPUT_CHANNEL_NAME_SIZE = 256
    const val INPUT_CLUB_DESCRIPTION_SIZE = 1024
    const val INPUT_CLUB_SUMMARY_SIZE = 512
    const val INPUT_POST_MESSAGE_SIZE = 2048
    const val INPUT_URL_SIZE = 512
}

fun SavedStateHandle.getLongArg(arg: String): Long = get<Long>(arg) ?: -1
fun SavedStateHandle.getStringArg(arg: String): String = get<String>(arg) ?: ""

fun Context.getMkdFormatter() = Markwon.builder(this)
    .usePlugin(StrikethroughPlugin.create())
    .usePlugin(MarkwonInlineParserPlugin.create())
    .usePlugin(LinkifyPlugin.create())
    .usePlugin(TablePlugin.create(this))
    .build()

fun String.isTrimmedNotEmpty(): Boolean = this.trim().isNotEmpty()
fun Int.lengthInRange(value: String): Boolean = value.length < this

fun Painter.zoomableContentLocation(): ZoomableContentLocation = object : ZoomableContentLocation {
    private fun Size.discardFractionalParts(): IntSize {
        return IntSize(width = width.toInt(), height = height.toInt())
    }

    override fun location(layoutSize: Size, direction: LayoutDirection): Rect {
        val heightScale = layoutSize.height / this@zoomableContentLocation.intrinsicSize.height
        val widthScale = layoutSize.width / this@zoomableContentLocation.intrinsicSize.width

        val scale = min(heightScale, widthScale)

        val scaledSize = this@zoomableContentLocation.intrinsicSize * scale
        val alignedOffset = Alignment.Center.align(
            size = scaledSize.discardFractionalParts(),
            space = layoutSize.discardFractionalParts(),
            layoutDirection = direction,
        )
        return Rect(
            offset = alignedOffset.toOffset(),
            size = scaledSize,
        )
    }

    override fun size(layoutSize: Size): Size = layoutSize
}

private val mMonthsList: List<String> = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
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

            val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
            val hour = if (hour24 > 12) hour24 - 12 else hour24
            val min = calendar.get(Calendar.MINUTE)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)

            "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"} " +
                    "${if (hour24 < 12) "AM" else "PM"}, $day ${mMonthsList[month]}"
        }
    }
}
