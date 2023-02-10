package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.ClubUrlModel
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
    suspend fun saveUrl(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int,
        @Body urlModel: UrlModel
    ): Response<ResponseBody>

    @POST("/url/{urlId}")
    suspend fun updateUrl(
        @Header("Authorization") auth: String?,
        @Path("urlId") urlID: Int,
        @Query("clubId") clubID: Int,
        @Body urlModel: UrlModel
    ): Response<ResponseBody>

    @DELETE("/url/{urlId}")
    suspend fun deleteUrl(
        @Header("Authorization") auth: String?,
        @Path("urlId") urlID: Int,
        @Query("clubId") clubID: Int,
        @Body clubUrlModel: ClubUrlModel
    ): Response<ResponseBody>
}