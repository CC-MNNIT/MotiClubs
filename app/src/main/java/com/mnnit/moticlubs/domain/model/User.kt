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
    val userId: Long,

    @ColumnInfo(name = "regNo")
    val regNo: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "course")
    val course: String,

    @ColumnInfo(name = "branch")
    val branch: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "contact")
    val contact: String,
) : Parcelable {
    constructor() : this(-1, "...", "...", "...", "...", "...", "", "")
    constructor(payload: Map<String, String>) : this(
        userId = payload["u_uid"]?.toLong() ?: throw Error("Invalid u_uid"),
        regNo = payload["u_regno"] ?: throw Error("Invalid u_regno"),
        name = payload["u_name"] ?: throw Error("Invalid u_name"),
        email = payload["u_email"] ?: throw Error("Invalid u_email"),
        course = payload["u_course"] ?: throw Error("Invalid u_course"),
        branch = payload["u_branch"] ?: throw Error("Invalid u_branch"),
        avatar = payload["u_avatar"] ?: throw Error("Invalid u_avatar"),
        contact = payload["u_contact"] ?: throw Error("Invalid u_contact"),
    )
}

@Parcelize
@Entity
data class AdminUser(
    @ColumnInfo(name = "uid")
    val userId: Long,

    @ColumnInfo(name = "cid")
    val clubId: Long,

    @ColumnInfo(name = "regNo")
    val regNo: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "course")
    val course: String,

    @ColumnInfo(name = "branch")
    val branch: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "contact")
    val contact: String,
) : Parcelable {
    constructor() : this(-1, -1, "...", "...", "...", "...", "...", "", "")

    fun getUser(): User = User(userId, regNo, name, email, course, branch, avatar, contact)
}
