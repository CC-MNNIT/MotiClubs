package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("pid")
    @Expose
    var postID: Long,

    @SerializedName("chid")
    @Expose
    var channelID: Long,

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
    var general: Int
)

data class SendPostDto(
    @SerializedName("pid")
    @Expose
    var postID: Long,

    @SerializedName("chid")
    @Expose
    var channelID: Long,

    @SerializedName("clubId")
    @Expose
    var clubID: Int,

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
    var general: Int
)

data class UpdatePostModel(
    @SerializedName("message")
    @Expose
    var message: String,
)
