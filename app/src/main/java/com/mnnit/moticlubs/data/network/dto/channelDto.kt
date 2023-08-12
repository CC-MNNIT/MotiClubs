package com.mnnit.moticlubs.data.network.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChannelDto(
    @SerializedName("chid")
    @Expose
    var channelId: Long,

    @SerializedName("cid")
    @Expose
    var clubId: Long,

    @SerializedName("name")
    @Expose
    var name: String
) : Parcelable

@Parcelize
data class UpdateChannelDto(
    @SerializedName("cid")
    @Expose
    var clubId: Long,

    @SerializedName("name")
    @Expose
    var name: String
) : Parcelable

@Parcelize
data class ChannelNavModel(
    @SerializedName("chid")
    @Expose
    var channelId: Long,

    @SerializedName("name")
    @Expose
    var name: String,
) : Parcelable
