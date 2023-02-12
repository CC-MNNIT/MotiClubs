package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.ViewCount
import com.mnnit.moticlubs.network.model.ViewPost
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
    ): Response<ViewCount>

    @POST("/views")
    suspend fun addView(
        @Header("Authorization") auth: String?,
        @Body viewPost: ViewPost
    ): Response<ResponseBody>
}