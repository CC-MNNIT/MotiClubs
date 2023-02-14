package com.mnnit.moticlubs.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ViewCountDto(
    @SerializedName("count")
    @Expose
    var count: Int
)

data class ViewPostDto(
    @SerializedName("postId")
    @Expose
    var postId: Int
)
