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

    @ColumnInfo(name = "private")
    val private: Int,
) : Parcelable {
    constructor() : this(-1L, -1, "...", 0)

    constructor(payload: Map<String, String>) : this(
        channelId = payload["ch_chid"]?.toLong() ?: throw Error("Invalid ch_chid"),
        clubId = payload["ch_cid"]?.toLong() ?: throw Error("Invalid ch_cid"),
        name = payload["ch_name"] ?: throw Error("Invalid ch_name"),
        private = payload["ch_private"]?.toInt() ?: throw Error("Invalid ch_private"),
    )
}
