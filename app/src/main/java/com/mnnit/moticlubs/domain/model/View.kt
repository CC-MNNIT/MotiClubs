package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "view", primaryKeys = ["uid", "pid"])
data class View(
    @ColumnInfo(name = "uid", index = true)
    val userID: Int,

    @ColumnInfo(name = "pid", index = true)
    val postID: Long
)
