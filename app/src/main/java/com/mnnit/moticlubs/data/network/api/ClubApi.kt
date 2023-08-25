package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ROUTE
import retrofit2.Response
import retrofit2.http.*

interface ClubApi {

    @GET(CLUB_ROUTE)
    suspend fun getClubs(@Header(AUTHORIZATION_HEADER) auth: String?): Response<List<ClubModel>?>

    @PUT("$CLUB_ROUTE/{$CLUB_ID_CLAIM}")
    suspend fun updateClub(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(CLUB_ID_CLAIM) clubId: Long,
        @Body data: UpdateClubDto,
    ): Response<ClubModel?>
}
