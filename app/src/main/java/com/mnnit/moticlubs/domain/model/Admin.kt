package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "admin", primaryKeys = ["uid", "cid"])
data class Admin(
    @ColumnInfo(name = "uid", index = true)
    val userID: Int,

    @ColumnInfo(name = "cid", index = true)
    val clubID: Int
)
