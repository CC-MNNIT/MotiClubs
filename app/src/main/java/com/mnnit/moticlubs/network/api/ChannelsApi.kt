package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.AddChannelModel
import com.mnnit.moticlubs.network.model.ChannelModel
import com.mnnit.moticlubs.network.model.UpdateChannelModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET("/channel")
    suspend fun getAllChannels(@Header("Authorization") auth: String?): Response<List<ChannelModel>?>

    @GET("/channel")
    suspend fun getClubChannels(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int
    ): Response<List<ChannelModel>?>

    @POST("/channel")
    suspend fun createChannel(
        @Header("Authorization") auth: String?,
        @Body addChannelModel: AddChannelModel
    ): Response<ResponseBody>

    @PUT("/channel/{channelId}")
    suspend fun updateChannelName(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelID: Int,
        @Body updateChannelModel: UpdateChannelModel
    ): Response<ResponseBody>

    @DELETE("/channel")
    suspend fun deleteChannel(
        @Header("Authorization") auth: String?,
        @Query("channelId") channelID: Int
    ): Response<ResponseBody>
}
