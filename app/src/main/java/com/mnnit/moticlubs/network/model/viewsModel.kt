package com.mnnit.moticlubs.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ViewCount(
    @SerializedName("count")
    @Expose
    var count: Int
)

data class ViewPost(
    @SerializedName("postId")
    @Expose
    var postId: Int
)
