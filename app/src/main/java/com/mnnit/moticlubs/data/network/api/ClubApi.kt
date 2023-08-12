package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.SubscriberDto
import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ROUTE
import retrofit2.Response
import retrofit2.http.*

interface ClubApi {

    @GET(CLUB_ROUTE)
    suspend fun getClubs(@Header(AUTHORIZATION_HEADER) auth: String?): Response<List<ClubModel>?>

    @GET("$CLUB_ROUTE/subscribers/{$CLUB_ID_CLAIM}")
    suspend fun getSubscribers(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubId: Long,
    ): Response<List<SubscriberDto>?>

    @PUT("$CLUB_ROUTE/{$CLUB_ID_CLAIM}")
    suspend fun updateClub(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubId: Long,
        @Body data: UpdateClubDto,
    ): Response<ClubModel?>
}
