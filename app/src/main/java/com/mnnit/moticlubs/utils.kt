package com.mnnit.moticlubs

import android.content.Context

object Constants {
    const val ADMIN_NAME = "admin_name"
    const val TIME = "time"
    const val MESSAGE = "message"
    const val AVATAR = "avatar"
    const val SHARED_PREFERENCE = "com.mnnit.moticlubs"
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

fun String.getDomainMail(): String = "$this@mnnit.ac.in"

fun Context.setAuthToken(token: String) =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .edit().putString(Constants.TOKEN, token).apply()

fun Context.getAuthToken(): String =
    this.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        .getString(Constants.TOKEN, "") ?: ""
