package com.mnnit.moticlubs.data.network.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClubModel(
    @SerializedName("cid")
    @Expose
    var clubId: Long,

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
) : Parcelable

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
