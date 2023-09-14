package com.mnnit.moticlubs.data.network.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GithubContributorDto(
    @SerializedName("login")
    @Expose
    var loginName: String,

    @SerializedName("avatar_url")
    @Expose
    var avatar: String,

    @SerializedName("html_url")
    @Expose
    var htmlUrl: String,

    @SerializedName("contributions")
    @Expose
    var contributions: Long,
) : Parcelable
