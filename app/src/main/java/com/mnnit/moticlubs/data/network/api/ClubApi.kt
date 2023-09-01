package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ROUTE
import com.mnnit.moticlubs.domain.util.Constants.STAMP_HEADER
import retrofit2.Response
import retrofit2.http.*

interface ClubApi {

    @GET(CLUB_ROUTE)
    suspend fun getClubs(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
    ): Response<List<ClubModel>?>

    @PUT("$CLUB_ROUTE/{$CLUB_ID_CLAIM}")
    suspend fun updateClub(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(CLUB_ID_CLAIM) clubId: Long,
        @Body data: UpdateClubDto,
    ): Response<ClubModel?>
}
