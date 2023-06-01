package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.domain.util.Constants.URL_PREFIX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET("${URL_PREFIX}channel")
    suspend fun getAllChannels(@Header("Authorization") auth: String?): Response<List<ChannelDto>?>

    @POST("${URL_PREFIX}channel")
    suspend fun createChannel(
        @Header("Authorization") auth: String?,
        @Body channelDto: ChannelDto
    ): Response<ResponseBody?>

    @PUT("${URL_PREFIX}channel/{channelId}")
    suspend fun updateChannelName(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Long,
        @Body channelDto: ChannelDto
    ): Response<ResponseBody?>

    @DELETE("${URL_PREFIX}channel/{channelId}")
    suspend fun deleteChannel(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Long
    ): Response<ResponseBody?>
}
