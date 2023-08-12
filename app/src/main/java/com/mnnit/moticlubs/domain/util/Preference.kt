package com.mnnit.moticlubs.domain.util

import android.content.Context
import com.mnnit.moticlubs.domain.model.Channel

fun Context.setUserID(userID: Long) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putLong(Constants.USER_ID, userID).apply()
}

fun Context.getUserID(): Long =
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

fun Context.postRead(channelID: Long, postID: Long, read: Boolean = false) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
        .putStringSet("ch$channelID", getUnreadPost(channelID).apply {
            if (read) remove(postID.toString()) else add(postID.toString())
        }).apply()
}

fun Context.getUnreadPost(channelID: Long) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .getStringSet("ch$channelID", setOf())
        ?.toMutableSet() ?: setOf<String>().toMutableSet()

fun Context.getExpandedChannel(clubID: Long): Boolean =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getBoolean("cl$clubID", false)

fun Context.setExpandedChannel(clubID: Long, expanded: Boolean) {
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .edit().putBoolean("cl$clubID", expanded).apply()
}
