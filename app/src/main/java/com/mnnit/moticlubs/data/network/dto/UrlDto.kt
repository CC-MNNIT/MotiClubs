package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UrlResponseModel(
    @SerializedName("urlId")
    @Expose
    var urlId: Long,

    @SerializedName("cid")
    @Expose
    var clubId: Long,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("color")
    @Expose
    var color: String = "FFFFFF",

    @SerializedName("url")
    @Expose
    var url: String,
)

data class UrlDto(
    @SerializedName("urls")
    @Expose
    var list: List<UrlModel>,
)

data class UrlModel(
    @SerializedName("urlId")
    @Expose
    var urlId: Long,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("color")
    @Expose
    var color: String,

    @SerializedName("url")
    @Expose
    var url: String,
)
