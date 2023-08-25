package com.mnnit.moticlubs.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "member", primaryKeys = ["uid", "chid"])
data class Member(
    @ColumnInfo(name = "uid", index = true)
    val userId: Long,

    @ColumnInfo(name = "chid", index = true)
    val channelId: Long,
) {
    constructor() : this(-1, -1)
}
