package com.mnnit.moticlubs.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveUserDto(
    @SerializedName("regno")
    @Expose
    var regno: String,

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

data class UserResponse(
    @SerializedName("uid")
    @Expose
    var id: Int,

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
    var avatar: String = "",

    @SerializedName("admin")
    @Expose
    var admin: List<UserClubDto>,

    @SerializedName("subscribed")
    @Expose
    var subscribed: MutableList<UserClubDto>,
) {
    constructor() : this(-1, "", "", "", "", "", "", listOf(), mutableListOf())
}

data class UpdateUserAvatarDto(
    @SerializedName("avatar")
    @Expose
    var avatar: String
)

data class UserClubDto(
    @SerializedName("clubId")
    @Expose
    var clubID: Int
)

data class FCMTokenDto(
    @SerializedName("token")
    @Expose
    var token: String
)

data class AdminDetailResponse(
    @SerializedName("userId")
    @Expose
    var uid: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("phone")
    @Expose
    var phoneNumber: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String
) {
    constructor() : this(-1, "", "", "", "")
}
