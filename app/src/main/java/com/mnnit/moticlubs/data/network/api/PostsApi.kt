package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.PostDto
import com.mnnit.moticlubs.data.network.dto.SendPostDto
import com.mnnit.moticlubs.data.network.dto.UpdatePostModel
import com.mnnit.moticlubs.domain.util.Constants.URL_PREFIX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {

    @GET("${URL_PREFIX}posts")
    suspend fun getPostsFromClubChannel(
        @Header("Authorization") auth: String?,
        @Query("channelId") channelID: Long,
        @Query("page") page: Int,
        @Query("items") items: Int = 10
    ): Response<List<PostDto>?>

    @POST("${URL_PREFIX}posts")
    suspend fun sendPost(
        @Header("Authorization") auth: String?,
        @Body postModel: SendPostDto
    ): Response<ResponseBody?>

    @PUT("${URL_PREFIX}posts/{postId}")
    suspend fun updatePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Long,
        @Body postModel: UpdatePostModel
    ): Response<ResponseBody?>

    @DELETE("${URL_PREFIX}posts/{postId}")
    suspend fun deletePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Long,
        @Query("channelId") channelID: Long
    ): Response<ResponseBody?>
}
