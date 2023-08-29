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
    val postId: Long,

    @ColumnInfo(name = "chid", index = true)
    val channelId: Long,

    @ColumnInfo(name = "page")
    val pageNo: Int,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "uid", index = true)
    val userId: Long,
) {
    constructor() : this(-1L, -1L, 1, "...", -1L)
}
