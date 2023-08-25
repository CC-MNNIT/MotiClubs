package com.mnnit.moticlubs.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MemberDto(
    @SerializedName("uid")
    @Expose
    var userId: Long,

    @SerializedName("chid")
    @Expose
    var channelId: Long,
)

data class AddMemberDto(
    @SerializedName("cid")
    @Expose
    var clubId: Long,

    @SerializedName("chid")
    @Expose
    var channelId: Long,

    @SerializedName("users")
    @Expose
    var users: List<Long>,
)
