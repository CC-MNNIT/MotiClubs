package com.mnnit.moticlubs.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChannelDto(
    @SerializedName("chid")
    @Expose
    var channelID: Int,

    @SerializedName("cid")
    @Expose
    var clubID: Int,

    @SerializedName("name")
    @Expose
    var name: String
) : Parcelable

@Parcelize
data class ChannelNavModel(
    @SerializedName("chid")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String,
) : Parcelable {
    constructor() : this(-1, "")
}

data class AddChannelDto(
    @SerializedName("clubId")
    @Expose
    var clubID: Int,

    @SerializedName("channelName")
    @Expose
    var name: String
)

data class UpdateChannelDto(
    @SerializedName("channelName")
    @Expose
    var name: String
)
