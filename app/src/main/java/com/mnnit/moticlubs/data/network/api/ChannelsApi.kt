package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ChannelDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET("channel")
    suspend fun getAllChannels(@Header("Authorization") auth: String?): Response<List<ChannelDto>?>

    @POST("channel")
    suspend fun createChannel(
        @Header("Authorization") auth: String?,
        @Body channelDto: ChannelDto
    ): Response<ResponseBody?>

    @PUT("channel/{channelId}")
    suspend fun updateChannelName(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Long,
        @Body channelDto: ChannelDto
    ): Response<ResponseBody?>

    @DELETE("channel/{channelId}")
    suspend fun deleteChannel(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Long
    ): Response<ResponseBody?>
}
