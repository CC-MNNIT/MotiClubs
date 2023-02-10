package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.ClubModel
import com.mnnit.moticlubs.network.model.SubscriberCountResponse
import com.mnnit.moticlubs.network.model.UpdateClubModel
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
        @Body data: UpdateClubModel
    ): Response<ResponseBody>

    @GET("/clubs/subscribers-count/{clubId}")
    suspend fun getSubscribersCount(
        @Header("Authorization") auth: String?,
        @Path("clubId") clubID: Int
    ): Response<SubscriberCountResponse?>
}
