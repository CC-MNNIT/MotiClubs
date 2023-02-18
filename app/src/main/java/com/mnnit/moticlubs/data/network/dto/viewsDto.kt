package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ViewDto(
    @SerializedName("pid")
    @Expose
    var postID: Long,

    @SerializedName("uid")
    @Expose
    var userID: Int
)
