package com.mnnit.moticlubs.network.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostNotificationModel(
    val clubName: String,
    val channelName: String,
    val channelID: Int,
    val postID: Int,
    val adminName: String,
    val adminAvatar: String,
    val message: String,
    val time: String,
) : Parcelable {
    constructor() : this("", "", -1, -1, "", "", "", "")
}

data class PostModel(
    @SerializedName("pid")
    @Expose
    var postID: Int,

    @SerializedName("cid")
    @Expose
    var clubID: Int,

    @SerializedName("chid")
    @Expose
    var channelID: Int,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("time")
    @Expose
    var time: Long,

    @SerializedName("uid")
    @Expose
    var userID: Int,

    @SerializedName("general")
    @Expose
    var general: Int,
) {
    constructor() : this(-1, -1, -1, "", 0L, -1, 0)
}

data class PushPostModel(
    @SerializedName("clubId")
    @Expose
    var clubID: Int,

    @SerializedName("channelId")
    @Expose
    var channelID: Int,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("general")
    @Expose
    var general: Boolean
)

data class UpdatePostModel(
    @SerializedName("message")
    @Expose
    var message: String,
)
