package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.data.network.dto.UpdateChannelDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ROUTE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET(CHANNEL_ROUTE)
    suspend fun getAllChannels(@Header(AUTHORIZATION_HEADER) auth: String?): Response<List<ChannelDto>?>

    @POST(CHANNEL_ROUTE)
    suspend fun createChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body channelDto: ChannelDto,
    ): Response<ChannelDto?>

    @PUT("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun updateChannelName(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelId: Long,
        @Body updateChannelDto: UpdateChannelDto,
    ): Response<ChannelDto?>

    @DELETE("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun deleteChannel(
        @Header("Authorization") auth: String?,
        @Path("channelId") channelId: Long,
        @Query("clubId") clubId: Long,
    ): Response<ResponseBody?>
}
