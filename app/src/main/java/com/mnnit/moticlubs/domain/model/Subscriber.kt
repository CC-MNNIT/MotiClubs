package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "subscriber", primaryKeys = ["uid", "cid"])
data class Subscriber(
    @ColumnInfo(name = "uid", index = true)
    val userId: Long,

    @ColumnInfo(name = "cid", index = true)
    val clubId: Long,
) {
    constructor() : this(-1, -1)
}
