package com.example.notificationapp.data.network.api

import com.example.notificationapp.data.network.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @POST("/user")
    fun saveUser(@Header("Authorization") auth: String?, @Body userModel: UserModel?): Call<UserResponse?>

    @GET("/clubs")
    fun getClubs(@Header("Authorization") auth: String?): Call<List<ClubModel>?>

    @GET("/user")
    fun getUserData(@Header("Authorization") auth: String?): Call<UserResponse?>

    @POST("/user/avatar")
    fun updateProfilePic(@Header("Authorization") auth: String?, @Body avatar: ProfilePicResponse): Call<ProfilePicResponse?>

    @POST("/user/fcmtoken")
    fun setFCMToken(@Header("Authorization") auth: String?, @Body token: FCMToken): Call<FCMToken?>

    @POST("/posts/{club}")
    fun postMessage(
        @Header("Authorization") auth: String?,
        @Path("club") clubID: String,
        @Body body: PostModel
    ): Call<PostResponse?>

    @GET("/posts/{club}")
    fun getClubPosts(
        @Header("Authorization") auth: String?,
        @Path("club") clubID: String,
    ): Call<List<PostResponse>?>

    @GET("/user/{email}")
    fun getAdminDetails(
        @Header("Authorization") auth: String?,
        @Path("email") email: String,
    ): Call<AdminResponse?>

    @PUT("/user/subscribe")
    fun subscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: ClubSubscriptionModel
    ): Call<ClubSubscriptionModel?>

    @PUT("/user/unsubscribe")
    fun unsubscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: ClubSubscriptionModel
    ): Call<ClubSubscriptionModel?>
}
