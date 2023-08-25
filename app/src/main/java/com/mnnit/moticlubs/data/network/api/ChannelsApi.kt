package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.AddMemberDto
import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.data.network.dto.MemberDto
import com.mnnit.moticlubs.data.network.dto.UpdateChannelDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ROUTE
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET(CHANNEL_ROUTE)
    suspend fun getAllChannels(@Header(AUTHORIZATION_HEADER) auth: String?): Response<List<ChannelDto>?>

    @GET("$CHANNEL_ROUTE/members/{$CHANNEL_ID_CLAIM}")
    suspend fun getMembers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
    ): Response<List<MemberDto>?>

    @POST("$CHANNEL_ROUTE/members")
    suspend fun addMembers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body dto: AddMemberDto,
    ): Response<List<MemberDto>?>

    @DELETE("$CHANNEL_ROUTE/members")
    suspend fun removeMembers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body dto: AddMemberDto,
    ): Response<ResponseBody?>

    @POST(CHANNEL_ROUTE)
    suspend fun createChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body channelDto: ChannelDto,
    ): Response<ChannelDto?>

    @PUT("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun updateChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
        @Body updateChannelDto: UpdateChannelDto,
    ): Response<ChannelDto?>

    @DELETE("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun deleteChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
        @Query(CLUB_ID_CLAIM) clubId: Long,
    ): Response<ResponseBody?>
}
