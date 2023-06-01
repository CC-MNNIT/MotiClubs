package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ReplyDto
import com.mnnit.moticlubs.domain.util.Constants.URL_PREFIX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ReplyApi {

    @GET("${URL_PREFIX}reply")
    suspend fun getReplies(
        @Header("Authorization") auth: String?,
        @Query("postId") postID: Long
    ): Response<List<ReplyDto>?>

    @POST("${URL_PREFIX}reply")
    suspend fun postReply(
        @Header("Authorization") auth: String?,
        @Body replyDto: ReplyDto
    ): Response<ResponseBody?>

    @DELETE("${URL_PREFIX}reply")
    suspend fun deleteReply(
        @Header("Authorization") auth: String?,
        @Query("replyId") replyID: Long
    ): Response<ResponseBody?>
}
