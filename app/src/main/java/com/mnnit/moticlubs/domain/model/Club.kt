package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "club")
data class Club(
    @PrimaryKey
    @ColumnInfo(name = "cid", index = true)
    val clubID: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "summary")
    val summary: String
) : Parcelable {
    constructor() : this(-1, "", "", "", "")
}
