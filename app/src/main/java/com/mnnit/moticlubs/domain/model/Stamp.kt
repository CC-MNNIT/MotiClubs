package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stamp")
data class Stamp(
    @PrimaryKey
    @ColumnInfo(name = "header", index = true)
    val header: String,

    @ColumnInfo(name = "stamp", index = true)
    val stamp: Long,
) : Parcelable {
    constructor() : this("", -1L)
}
