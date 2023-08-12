package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "view", primaryKeys = ["uid", "pid"])
data class View(
    @ColumnInfo(name = "uid", index = true)
    val userId: Long,

    @ColumnInfo(name = "pid", index = true)
    val postId: Long,
)
