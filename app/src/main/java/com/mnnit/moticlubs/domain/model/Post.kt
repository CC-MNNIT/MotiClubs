package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post")
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "pid", index = true)
    val postId: Long,

    @ColumnInfo(name = "chid", index = true)
    val channelId: Long,

    @ColumnInfo(name = "updated")
    val updated: Long,

    @ColumnInfo(name = "page")
    val pageNo: Int,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "uid", index = true)
    val userId: Long,
) {
    constructor() : this(-1L, -1L, -1L, 1, "...", -1L)

    constructor(payload: Map<String, String>) : this(
        postId = payload["p_pid"]?.toLong() ?: throw Error("Invalid p_pid"),
        userId = payload["p_uid"]?.toLong() ?: throw Error("Invalid p_uid"),
        channelId = payload["p_chid"]?.toLong() ?: throw Error("Invalid p_chid"),
        updated = payload["p_updated"]?.toLong() ?: throw Error("Invalid p_updated"),
        message = payload["p_message"] ?: throw Error("Invalid p_message"),
        pageNo = 1,
    )
}
