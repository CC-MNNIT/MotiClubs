package com.example.notificationapp

object Constants {
    const val SHARED_PREFERENCE = "com.example.notificationapp"
    const val TOKEN = "token"
    const val EMAIL = "email"
    const val BASE_URL = "https://notification-app-mnnit.vercel.app"
    const val CLUB_NAME = "club_name"
    const val CLUB_ID = "club_id"
    const val CLUB_DESC = "club_desc"
}

fun String.isNotValidDomain(): Boolean = !"^([^\\s]+)@mnnit.ac.in$".toRegex().containsMatchIn(this)