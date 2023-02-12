package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.ClubUrlModel
import com.mnnit.moticlubs.network.model.UrlDto
import com.mnnit.moticlubs.network.model.UrlModel
import com.mnnit.moticlubs.network.model.UrlResponseModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UrlApi {

    @GET("/url")
    suspend fun getUrls(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int
    ): Response<List<UrlResponseModel>?>

    @POST("/url")
    suspend fun pushUrls(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int,
        @Body urlDto: UrlDto
    ): Response<ResponseBody>
}