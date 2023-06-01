package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("uid")
    @Expose
    val uid: Long,

    @SerializedName("regno")
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

    @SerializedName("phone")
    @Expose
    val phone: String,

    @SerializedName("avatar")
    @Expose
    val avatar: String = ""
)

data class SaveUserDto(
    @SerializedName("regno")
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

    @SerializedName("phone")
    @Expose
    var phone: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String = ""
)

data class UpdateUserAvatarDto(
    @SerializedName("avatar")
    @Expose
    var avatar: String
)

data class SubscribedClubDto(
    @SerializedName("clubId")
    @Expose
    var clubID: Int
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

    @SerializedName("regno")
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

    @SerializedName("phone")
    @Expose
    var phoneNumber: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("cid")
    @Expose
    var clubID: Int
)
