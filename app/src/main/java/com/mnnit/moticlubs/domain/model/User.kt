package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "uid", index = true)
    val userID: Long,

    @ColumnInfo(name = "regno")
    val regNo: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "course")
    val course: String,

    @ColumnInfo(name = "phone")
    val phoneNumber: String,

    @ColumnInfo(name = "avatar")
    val avatar: String
) : Parcelable {
    constructor() : this(-1, "", "", "", "", "", "")
}
