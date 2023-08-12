package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "channel")
data class Channel(
    @PrimaryKey
    @ColumnInfo(name = "chid", index = true)
    val channelId: Long,

    @ColumnInfo(name = "cid", index = true)
    val clubId: Long,

    @ColumnInfo(name = "name")
    val name: String,
) : Parcelable {
    constructor() : this(-1L, -1, "")
}
