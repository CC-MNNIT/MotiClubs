package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.PostDto
import com.mnnit.moticlubs.data.network.dto.SendPostDto
import com.mnnit.moticlubs.data.network.dto.UpdatePostModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {

    @GET("/posts")
    suspend fun getPostsFromClubChannel(
        @Header("Authorization") auth: String?,
        @Query("channelId") channelID: Long
    ): Response<List<PostDto>?>

    @POST("/posts")
    suspend fun sendPost(
        @Header("Authorization") auth: String?,
        @Body postModel: SendPostDto
    ): Response<ResponseBody?>

    @PUT("/posts/{postId}")
    suspend fun updatePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Long,
        @Body postModel: UpdatePostModel
    ): Response<ResponseBody?>

    @DELETE("/posts/{postId}")
    suspend fun deletePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Long,
        @Query("channelId") channelID: Long
    ): Response<ResponseBody?>
}
