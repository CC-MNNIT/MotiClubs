package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "subscriber", primaryKeys = ["uid", "cid"])
data class Subscriber(
    @ColumnInfo(name = "uid", index = true)
    val userID: Long,

    @ColumnInfo(name = "cid", index = true)
    val clubID: Int
) {
    constructor() : this(-1, -1)
}
