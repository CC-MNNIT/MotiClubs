package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.SubscriberDto
import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.util.Constants.URL_PREFIX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ClubApi {

    @GET("${URL_PREFIX}clubs")
    suspend fun getClubs(@Header("Authorization") auth: String?): Response<List<ClubModel>?>

    @PUT("${URL_PREFIX}clubs/{clubId}")
    suspend fun updateClub(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubID: Int,
        @Body data: UpdateClubDto
    ): Response<ResponseBody?>

    @GET("${URL_PREFIX}clubs/subscribers/{clubId}")
    suspend fun getSubscribers(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubID: Int
    ): Response<List<SubscriberDto>?>
}
