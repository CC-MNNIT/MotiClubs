package com.mnnit.moticlubs.domain.util

import android.content.Context
import com.mnnit.moticlubs.domain.model.Channel

fun Context.setUserId(userId: Long) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putLong(Constants.USER_ID, userId).apply()
}

fun Context.getUserId(): Long =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getLong(Constants.USER_ID, -1)

fun Context.setAuthToken(token: String) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putString(Constants.TOKEN, token).apply()

fun Context.getAuthToken(): String =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(Constants.TOKEN, "") ?: ""

fun Context.clubHasUnreadPost(channels: List<Channel>): Boolean {
    var has = false
    for (i in channels.indices) {
        if (getUnreadPost(channels[i].channelId).isNotEmpty()) {
            has = true
            break
        }
    }
    return has
}

fun Context.postRead(channelId: Long, postId: Long, read: Boolean = false) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putStringSet("ch$channelId", getUnreadPost(channelId).apply {
            if (read) remove(postId.toString()) else add(postId.toString())
        }).apply()
}

fun Context.getUnreadPost(channelId: Long) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .getStringSet("ch$channelId", setOf())
        ?.toMutableSet() ?: setOf<String>().toMutableSet()

fun Context.getExpandedChannel(clubId: Long): Boolean =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getBoolean("cl$clubId", false)

fun Context.setExpandedChannel(clubId: Long, expanded: Boolean) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .edit().putBoolean("cl$clubId", expanded).apply()
}
