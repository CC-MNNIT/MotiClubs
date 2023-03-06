package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "post")
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "pid", index = true)
    val postID: Long,

    @ColumnInfo(name = "chid", index = true)
    val channelID: Long,

    @ColumnInfo(name = "page")
    val pageNo: Int,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "time")
    val time: Long,

    @ColumnInfo(name = "uid", index = true)
    val userID: Int
) {
    constructor() : this(-1L, -1L, 0, "", 0L, -1)
}

@Parcelize
data class PostNotificationModel(
    val clubName: String,
    val channelName: String,
    val channelID: Long,
    val postID: Long,
    val userID: Int,
    val adminName: String,
    val adminAvatar: String,
    val message: String,
    val time: String,
) : Parcelable {
    constructor() : this("", "", -1, -1, -1, "", "", "", "")
}
