package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "club")
data class Club(
    @PrimaryKey
    @ColumnInfo(name = "cid", index = true)
    val clubId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "summary")
    val summary: String,
) : Parcelable {
    constructor() : this(-1, "...", "...", "", "...")

    constructor(payload: Map<String, String>) : this(
        clubId = payload["c_cid"]?.toLong() ?: throw Error("Invalid c_cid"),
        name = payload["c_name"] ?: throw Error("Invalid c_name"),
        description = payload["c_description"] ?: throw Error("Invalid c_description"),
        summary = payload["c_summary"] ?: throw Error("Invalid c_summary"),
        avatar = payload["c_avatar"] ?: throw Error("Invalid c_avatar"),
    )
}
