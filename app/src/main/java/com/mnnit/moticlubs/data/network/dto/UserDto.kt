package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("uid")
    @Expose
    val uid: Long,

    @SerializedName("regNo")
    @Expose
    val regNo: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("course")
    @Expose
    val course: String,

    @SerializedName("branch")
    @Expose
    val branch: String,

    @SerializedName("avatar")
    @Expose
    val avatar: String = "",

    @SerializedName("contact")
    @Expose
    val contact: String = "",
)

data class SaveUserDto(
    @SerializedName("regNo")
    @Expose
    var regNo: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("course")
    @Expose
    var course: String,

    @SerializedName("branch")
    @Expose
    var branch: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String = "",

    @SerializedName("contact")
    @Expose
    val contact: String = "",
)

data class UpdateUserAvatarDto(
    @SerializedName("avatar")
    @Expose
    var avatar: String,
)

data class UpdateUserContactDto(
    @SerializedName("contact")
    @Expose
    var contact: String,
)

data class FCMDto(
    @SerializedName("uid")
    @Expose
    var userId: String,

    @SerializedName("token")
    @Expose
    var token: String,
)

data class FCMTokenDto(
    @SerializedName("token")
    @Expose
    var token: String
)

data class AdminDetailDto(
    @SerializedName("uid")
    @Expose
    var uid: Long,

    @SerializedName("regNo")
    @Expose
    var regNo: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("course")
    @Expose
    var course: String,

    @SerializedName("branch")
    @Expose
    var branch: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("cid")
    @Expose
    var clubId: Long,

    @SerializedName("contact")
    @Expose
    val contact: String = "",
)
