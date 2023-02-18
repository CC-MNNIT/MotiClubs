package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("/user")
    suspend fun saveUser(
        @Header("Authorization") auth: String?,
        @Body saveUserDto: SaveUserDto?
    ): Response<ResponseBody?>

    @GET("/user/admins")
    suspend fun getAllAdmins(@Header("Authorization") auth: String?): Response<List<AdminDetailResponse>?>

    @GET("/user/{userId}")
    suspend fun getUserDetails(
        @Header("Authorization") auth: String?,
        @Path("userId") userID: Int,
    ): Response<AdminDetailResponse?>

    @POST("/user/avatar")
    suspend fun setProfilePicUrl(
        @Header("Authorization") auth: String?,
        @Body avatar: UpdateUserAvatarDto
    ): Response<ResponseBody?>

    @POST("/user/fcmtoken")
    suspend fun setFCMToken(
        @Header("Authorization") auth: String?,
        @Body token: FCMTokenDto
    ): Response<ResponseBody?>

    @POST("/user/subscribe")
    suspend fun subscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: SubscribedClubDto
    ): Response<ResponseBody?>

    @POST("/user/unsubscribe")
    suspend fun unsubscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: SubscribedClubDto
    ): Response<ResponseBody?>
}
