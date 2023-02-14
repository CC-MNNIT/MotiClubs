package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.model.ViewCountDto
import com.mnnit.moticlubs.data.network.model.ViewPostDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ViewsApi {

    @GET("/views")
    suspend fun getViews(
        @Header("Authorization") auth: String?,
        @Query("postId") postID: Int
    ): Response<ViewCountDto>

    @POST("/views")
    suspend fun addView(
        @Header("Authorization") auth: String?,
        @Body viewPostDto: ViewPostDto
    ): Response<ResponseBody>
}