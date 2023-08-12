package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.PostDto
import com.mnnit.moticlubs.data.network.dto.UpdatePostModel
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CHANNEL_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.POST_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.POST_ROUTE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {

    @GET(POST_ROUTE)
    suspend fun getPostsFromChannel(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CHANNEL_ID_CLAIM) channelId: Long,
        @Query("page") page: Int,
        @Query("items") items: Int = 10,
    ): Response<List<PostDto>?>

    @POST(POST_ROUTE)
    suspend fun sendPost(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Body postDto: PostDto,
    ): Response<PostDto?>

    @PUT("$POST_ROUTE/{$POST_ID_CLAIM}")
    suspend fun updatePost(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(POST_ID_CLAIM) postId: Long,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Body postModel: UpdatePostModel,
    ): Response<PostDto?>

    @DELETE("$POST_ROUTE/{$POST_ID_CLAIM}")
    suspend fun deletePost(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(POST_ID_CLAIM) postId: Long,
        @Query(CLUB_ID_CLAIM) clubId: Long,
    ): Response<ResponseBody?>
}
