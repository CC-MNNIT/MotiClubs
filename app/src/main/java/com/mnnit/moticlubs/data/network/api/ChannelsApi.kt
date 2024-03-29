package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.AddMemberDto
import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.data.network.dto.MemberDto
import com.mnnit.moticlubs.data.network.dto.UpdateChannelDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ROUTE
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.STAMP_HEADER
import com.mnnit.moticlubs.domain.util.Constants.USER_ID_CLAIM
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChannelsApi {

    @GET(CHANNEL_ROUTE)
    suspend fun getAllChannels(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
    ): Response<List<ChannelDto>?>

    @GET("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun getChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
    ): Response<ChannelDto?>

    @GET("$CHANNEL_ROUTE/members/{$CHANNEL_ID_CLAIM}")
    suspend fun getMembers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
    ): Response<List<MemberDto>?>

    @POST("$CHANNEL_ROUTE/members")
    suspend fun addMembers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Body dto: AddMemberDto,
    ): Response<List<MemberDto>?>

    @DELETE("$CHANNEL_ROUTE/members")
    suspend fun removeMember(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Query(CHANNEL_ID_CLAIM) channelId: Long,
        @Query(USER_ID_CLAIM) userId: Long,
    ): Response<ResponseBody?>

    @POST(CHANNEL_ROUTE)
    suspend fun createChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Body channelDto: ChannelDto,
    ): Response<ChannelDto?>

    @PUT("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun updateChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
        @Body updateChannelDto: UpdateChannelDto,
    ): Response<ChannelDto?>

    @DELETE("$CHANNEL_ROUTE/{$CHANNEL_ID_CLAIM}")
    suspend fun deleteChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(CHANNEL_ID_CLAIM) channelId: Long,
        @Query(CLUB_ID_CLAIM) clubId: Long,
    ): Response<ResponseBody?>
}
