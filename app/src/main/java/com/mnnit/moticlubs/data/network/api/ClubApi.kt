package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.model.ClubModel
import com.mnnit.moticlubs.data.network.model.SubscriberCountDto
import com.mnnit.moticlubs.data.network.model.UpdateClubDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ClubApi {

    @GET("/clubs")
    suspend fun getClubs(@Header("Authorization") auth: String?): Response<List<ClubModel>?>

    @PUT("/clubs/{clubId}")
    suspend fun updateClub(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubID: Int,
        @Body data: UpdateClubDto
    ): Response<ResponseBody>

    @GET("/clubs/subscribers-count/{clubId}")
    suspend fun getSubscribersCount(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubID: Int
    ): Response<SubscriberCountDto?>
}
