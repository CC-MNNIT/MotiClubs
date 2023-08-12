package com.mnnit.moticlubs.data.network.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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

@Parcelize
data class ImageUrl(
    val imageUrl: String
) : Parcelable
