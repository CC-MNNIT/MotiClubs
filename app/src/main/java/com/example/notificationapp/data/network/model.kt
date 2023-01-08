package com.example.notificationapp.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClubModel(
    @SerializedName("_id")
    @Expose
    var id: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("admins")
    @Expose
    var admins: List<String>
)

data class IntroSlide(
    var title: String,
    var description: String,
    var icon: Int
) {
    constructor() : this("", "", 0)
}

data class UserModel(
    var name: String,
    var registrationNumber: String,
    var graduationYear: String,
    var course: String,
    var email: String,
    var personalEmail: String,
    var phoneNumber: String,
    var avatar: String = ""
)

data class UserResponse(
    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("registrationNumber")
    @Expose
    var registrationNumber: String,

    @SerializedName("graduationYear")
    @Expose
    var graduationYear: String,

    @SerializedName("course")
    @Expose
    var course: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("personalEmail")
    @Expose
    var personalEmail: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String,

    @SerializedName("admin")
    @Expose
    var admin: List<String>,
)
