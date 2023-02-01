package com.mnnit.moticlubs.api

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("admins")
    @Expose
    var admins: List<String>,

    @SerializedName("socialUrls")
    @Expose
    var socialMedia: List<String>

) : Parcelable {
    constructor() : this("", "", "", "", listOf(), listOf())
}

@Parcelize
data class PostNotificationModel(
    val clubName: String,
    val clubID: String,
    val postID: String,
    val adminName: String,
    val adminAvatar: String,
    val message: String,
    val time: String,
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "")
}

class PostParamType : NavType<PostNotificationModel>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PostNotificationModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, PostNotificationModel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): PostNotificationModel =
        Gson().fromJson(value, PostNotificationModel::class.java)

    override fun put(bundle: Bundle, key: String, value: PostNotificationModel) {
        bundle.putParcelable(key, value)
    }
}

data class UserModel(
    var name: String,
    var registrationNumber: String,
    var course: String,
    var email: String,
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

    @SerializedName("course")
    @Expose
    var course: String,

    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String,

    @SerializedName("admin")
    @Expose
    var admin: List<String>,

    @SerializedName("subscribed")
    @Expose
    var subscribed: List<String>,
)

data class ProfilePicResponse(
    @SerializedName("avatar")
    @Expose
    var avatar: String
)

data class PostResponse(
    @SerializedName("_id")
    @Expose
    var id: String,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("time")
    @Expose
    var time: Long,

    @SerializedName("club")
    @Expose
    var club: String,

    @SerializedName("adminEmail")
    @Expose
    var adminEmail: String,
) {
    constructor() : this("", "", 0, "", "")
}

data class UserDetailResponse(
    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("personalEmail")
    @Expose
    var personalEmail: String,

    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String
) {
    constructor() : this("", "", "", "")
}

data class PostModel(var message: String, var club: String)
data class UpdatePostModel(var message: String)

data class FCMToken(
    @SerializedName("token")
    @Expose
    var token: String
)

data class ClubSubscriptionModel(
    @SerializedName("club")
    @Expose
    var club: String
)
