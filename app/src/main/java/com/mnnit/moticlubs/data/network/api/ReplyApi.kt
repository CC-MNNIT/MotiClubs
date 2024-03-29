package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ReplyDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.POST_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.REPLY_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.REPLY_ROUTE
import com.mnnit.moticlubs.domain.util.Constants.STAMP_HEADER
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ReplyApi {

    @GET(REPLY_ROUTE)
    suspend fun getReplies(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Query(POST_ID_CLAIM) postId: Long,
        @Query("page") page: Int,
        @Query("items") items: Int = 15,
    ): Response<List<ReplyDto>?>

    @POST(REPLY_ROUTE)
    suspend fun postReply(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Body replyDto: ReplyDto,
    ): Response<ReplyDto?>

    @DELETE(REPLY_ROUTE)
    suspend fun deleteReply(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Query(REPLY_ID_CLAIM) replyId: Long,
    ): Response<ResponseBody?>
}
