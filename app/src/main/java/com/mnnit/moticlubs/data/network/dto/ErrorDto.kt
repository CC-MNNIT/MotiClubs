package com.mnnit.moticlubs.data.network.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorDto(
    @SerializedName("timestamp")
    @Expose
    val timestamp: String,

    @SerializedName("path")
    @Expose
    val path: String,

    @SerializedName("status")
    @Expose
    val status: Int,

    @SerializedName("error")
    @Expose
    val error: String,

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("requestId")
    @Expose
    val requestId: String,
) : Parcelable {
    fun getErrorMessage(): String = "[$requestId] $message"
}
