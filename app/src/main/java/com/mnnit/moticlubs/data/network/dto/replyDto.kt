package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReplyDto(
    @SerializedName("pid")
    @Expose
    var postId: Long,

    @SerializedName("uid")
    @Expose
    var userId: Long,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("time")
    @Expose
    var time: Long,
)
