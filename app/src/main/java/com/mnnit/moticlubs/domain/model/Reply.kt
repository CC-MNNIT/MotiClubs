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

    @PrimaryKey
    @ColumnInfo(name = "time")
    val time: Long,
) : Parcelable
