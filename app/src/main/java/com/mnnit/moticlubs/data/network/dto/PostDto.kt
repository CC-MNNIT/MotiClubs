package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("pid")
    @Expose
    var postId: Long,

    @SerializedName("chid")
    @Expose
    var channelId: Long,

    @SerializedName("updated")
    @Expose
    var updated: Long,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("uid")
    @Expose
    var userId: Long,
)

data class UpdatePostModel(
    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("updated")
    @Expose
    var updated: Long,
)
