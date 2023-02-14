package com.mnnit.moticlubs.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClubModel(
    @SerializedName("cid")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("summary")
    @Expose
    var summary: String,

    @SerializedName("admins")
    @Expose
    var admins: List<ClubUserDto>,

    @SerializedName("channels")
    @Expose
    var channels: List<ChannelDto>
) : Parcelable

@Parcelize
data class ClubDetailModel(
    @SerializedName("cid")
    @Expose
    var id: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("summary")
    @Expose
    var summary: String,

    @SerializedName("admins")
    @Expose
    var admins: List<ClubUserDto>,

    @SerializedName("subscribers")
    @Expose
    var subscribers: Int
) : Parcelable {
    constructor() : this(-1, "", "", "", "", listOf(), 0)
}

@Parcelize
data class ClubNavModel(
    @SerializedName("cid")
    @Expose
    var clubId: Int,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("summary")
    @Expose
    var summary: String,

    @SerializedName("channel")
    @Expose
    var channel: ChannelNavModel,
) : Parcelable {
    constructor() : this(-1, "", "", "", "", ChannelNavModel())
}

data class UpdateClubDto(
    @SerializedName("description")
    @Expose
    var description: String,

    @SerializedName("avatar")
    @Expose
    var avatar: String,

    @SerializedName("summary")
    @Expose
    var summary: String,
)

data class SubscriberCountDto(
    @SerializedName("count")
    @Expose
    var count: Int
)

@Parcelize
data class ClubUserDto(
    @SerializedName("userId")
    @Expose
    var userID: Int
) : Parcelable
