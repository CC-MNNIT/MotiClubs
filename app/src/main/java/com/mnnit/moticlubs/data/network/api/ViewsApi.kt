package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ViewDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ViewsApi {

    @GET("views")
    suspend fun getViews(
        @Header("Authorization") auth: String?,
        @Query("postId") postID: Long
    ): Response<List<ViewDto>?>

    @POST("views")
    suspend fun addView(
        @Header("Authorization") auth: String?,
        @Body viewDto: ViewDto
    ): Response<ResponseBody?>
}
