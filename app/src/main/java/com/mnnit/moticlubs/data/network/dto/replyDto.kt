package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReplyDto(
    @SerializedName("pid")
    @Expose
    var postID: Long,

    @SerializedName("uid")
    @Expose
    var userID: Long,

    @SerializedName("to_uid")
    @Expose
    var toUID: Long,

    @SerializedName("message")
    @Expose
    var message: String,

    @SerializedName("time")
    @Expose
    var time: Long,
)
