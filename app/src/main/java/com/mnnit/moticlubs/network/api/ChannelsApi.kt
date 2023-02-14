package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.AddChannelDto
import com.mnnit.moticlubs.network.model.ChannelDto
import com.mnnit.moticlubs.network.model.UpdateChannelDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET("/channel")
    suspend fun getAllChannels(@Header("Authorization") auth: String?): Response<List<ChannelDto>?>

    @GET("/channel")
    suspend fun getClubChannels(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int
    ): Response<List<ChannelDto>?>

    @POST("/channel")
    suspend fun createChannel(
        @Header("Authorization") auth: String?,
        @Body addChannelDto: AddChannelDto
    ): Response<ResponseBody>

    @PUT("/channel/{channelId}")
    suspend fun updateChannelName(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Int,
        @Body updateChannelDto: UpdateChannelDto
    ): Response<ResponseBody>

    @DELETE("/channel/{channelId}")
    suspend fun deleteChannel(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Int
    ): Response<ResponseBody>
}
