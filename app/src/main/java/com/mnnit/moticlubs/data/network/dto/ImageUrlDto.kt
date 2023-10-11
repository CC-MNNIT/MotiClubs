package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImageUrlDto(
    @SerializedName("url")
    @Expose
    var url: String,
)
