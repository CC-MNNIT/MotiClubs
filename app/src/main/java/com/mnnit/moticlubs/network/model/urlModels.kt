package com.mnnit.moticlubs.network.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mnnit.moticlubs.ui.components.OtherLinkComposeModel
import com.mnnit.moticlubs.ui.components.SocialLinkComposeModel

data class UrlResponseModel(
    @SerializedName("urlId")
    @Expose
    var urlID: Int,

    @SerializedName("cid")
    @Expose
    var clubID: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("color")
    @Expose
    var color: String = "FFFFFF",

    @SerializedName("url")
    @Expose
    var url: String,
) {
    constructor() : this(-1, -1, "", "FFFFFF", "")

    fun mapToOtherLinkModel(): OtherLinkComposeModel = OtherLinkComposeModel(
        urlID, clubID,
        mutableStateOf(TextFieldValue("$name\\$url")),
        mutableStateOf("#$color"),
        mutableStateOf(Color(android.graphics.Color.parseColor("#$color")))
    )

    fun mapToSocialLinkModel(): SocialLinkComposeModel = SocialLinkComposeModel(
        urlID, clubID, name,
        mutableStateOf(TextFieldValue(url)),
        mutableStateOf("#$color"),
        mutableStateOf(Color(android.graphics.Color.parseColor("#$color")))
    )

    fun mapToUrlModel(): UrlModel = UrlModel(urlID, name, color.replace("#", ""), url)

    fun getLinkBadge(): String {
        return "[![$name](https://img.shields.io/badge/$name-%23$color.svg?style=for-the-badge)]($url) "
    }
}

data class UrlDto(
    @SerializedName("urls")
    @Expose
    var list: List<UrlModel>
)

data class UrlModel(
    @SerializedName("urlId")
    @Expose
    var urlID: Int,

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
