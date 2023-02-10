package com.mnnit.moticlubs.network.api

import com.mnnit.moticlubs.network.model.PostModel
import com.mnnit.moticlubs.network.model.PushPostModel
import com.mnnit.moticlubs.network.model.UpdatePostModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {

    @GET("/posts")
    suspend fun getPostsFromClubChannel(
        @Header("Authorization") auth: String?,
        @Query("clubId") clubID: Int,
        @Query("channelId") channelID: Int,
    ): Response<List<PostModel>?>

    @POST("/posts")
    suspend fun sendPost(
        @Header("Authorization") auth: String?,
        @Body postModel: PushPostModel
    ): Response<ResponseBody>

    @PUT("/posts/{postId}")
    suspend fun updatePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Int,
        @Body postModel: UpdatePostModel
    ): Response<ResponseBody>

    @DELETE("/posts/{postId}")
    suspend fun deletePost(
        @Header("Authorization") auth: String?,
        @Path("postId") postID: Int
    ): Response<ResponseBody>
}
