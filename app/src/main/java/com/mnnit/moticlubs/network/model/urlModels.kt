package com.mnnit.moticlubs.network.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mnnit.moticlubs.ui.components.LinkComposeModel

data class UrlResponseModel(
    @SerializedName("urlid")
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
    var color: String,

    @SerializedName("url")
    @Expose
    var url: String,
) {
    constructor() : this(-1, -1, "", "", "")

    fun map(): LinkComposeModel = LinkComposeModel(
        mutableStateOf(TextFieldValue("$name\\$url")),
        mutableStateOf("#$color"),
        mutableStateOf(Color(android.graphics.Color.parseColor("#$color")))
    )
}

data class UrlModel(
    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("color")
    @Expose
    var color: String,

    @SerializedName("url")
    @Expose
    var url: String,
) {
    constructor() : this("", "", "")
}

data class ClubUrlModel(
    @SerializedName("clubId")
    @Expose
    var clubID: Int
)
