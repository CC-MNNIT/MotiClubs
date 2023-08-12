package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "admin", primaryKeys = ["uid", "cid"])
data class Admin(
    @ColumnInfo(name = "uid", index = true)
    val userId: Long,

    @ColumnInfo(name = "cid", index = true)
    val clubId: Long,
)
