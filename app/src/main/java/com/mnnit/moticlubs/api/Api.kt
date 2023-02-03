package com.mnnit.moticlubs.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @POST("/user")
    suspend fun saveUser(@Header("Authorization") auth: String?, @Body userModel: UserModel?): Response<UserResponse?>

    @GET("/user")
    suspend fun getUserData(@Header("Authorization") auth: String?): Response<UserResponse?>

    @POST("/user/avatar")
    suspend fun updateProfilePic(
        @Header("Authorization") auth: String?,
        @Body avatar: ProfilePicResponse
    ): Response<ProfilePicResponse?>

    @POST("/user/fcmtoken")
    suspend fun setFCMToken(@Header("Authorization") auth: String?, @Body token: FCMToken): Response<FCMToken?>

    @GET("/user/{email}")
    suspend fun getUserDetails(
        @Header("Authorization") auth: String?,
        @Path("email") email: String,
    ): Response<UserDetailResponse?>

    @PUT("/user/subscribe")
    suspend fun subscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: ClubSubscriptionModel
    ): Response<ClubSubscriptionModel?>

    @PUT("/user/unsubscribe")
    suspend fun unsubscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: ClubSubscriptionModel
    ): Response<ClubSubscriptionModel?>

    @GET("/clubs")
    suspend fun getClubs(@Header("Authorization") auth: String?): Response<List<ClubModel>?>

    @GET("/posts")
    suspend fun getClubPosts(
        @Header("Authorization") auth: String?,
        @Query("club") clubID: String,
    ): Response<List<PostResponse>?>

    @POST("/posts")
    suspend fun sendPost(
        @Header("Authorization") auth: String?,
        @Body postModel: PostModel
    ): Response<PostResponse?>

    @PUT("/posts/{post}")
    suspend fun updatePost(
        @Header("Authorization") auth: String?,
        @Path("post") postID: String,
        @Body postModel: UpdatePostModel
    ): Response<ResponseBody>

    @DELETE("/posts/{post}")
    suspend fun deletePost(
        @Header("Authorization") auth: String?,
        @Path("post") postID: String
    ): Response<ResponseBody>

    @PUT("/clubs/{club}")
    suspend fun updateClub(
        @Header("Authorization") auth: String?,
        @Path("club") clubID: String,
        @Body clubDTO: ClubDTO
    ): Response<ResponseBody>

    @GET("/clubs/subscribers-count/{club}")
    suspend fun getMembersCount(
        @Header("Authorization") auth: String?,
        @Path("club") clubID: String
    ): Response<SubscriberCountResponse>
}
