package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "reply")
data class Reply(
    @ColumnInfo(name = "pid", index = true)
    val postId: Long,

    @ColumnInfo(name = "uid", index = true)
    val userId: Long,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "page")
    val pageNo: Int,

    @PrimaryKey
    @ColumnInfo(name = "time")
    val time: Long,
) : Parcelable {
    constructor() : this(-1L, -1L, "", 0, -1L)
    constructor(payload: Map<String, String>) : this(
        time = payload["r_time"]?.toLong() ?: throw Error("Invalid r_time"),
        postId = payload["r_pid"]?.toLong() ?: throw Error("Invalid r_pid"),
        userId = payload["r_uid"]?.toLong() ?: throw Error("Invalid r_uid"),
        message = payload["r_message"] ?: throw Error("Invalid r_message"),
        pageNo = 1,
    )
}
