package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.*
import com.mnnit.moticlubs.domain.util.Constants.URL_PREFIX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("${URL_PREFIX}user")
    suspend fun saveUser(
        @Header("Authorization") auth: String?,
        @Body saveUserDto: SaveUserDto?
    ): Response<ResponseBody?>

    @GET("${URL_PREFIX}user/all")
    suspend fun getAllUsers(@Header("Authorization") auth: String?): Response<List<UserDto>?>

    @GET("${URL_PREFIX}user/admins")
    suspend fun getAllAdmins(@Header("Authorization") auth: String?): Response<List<AdminDetailDto>?>

    @GET("${URL_PREFIX}user/{userId}")
    suspend fun getUserDetails(
        @Header("Authorization") auth: String?,
        @Path("userId") userID: Long,
    ): Response<AdminDetailDto?>

    @POST("${URL_PREFIX}user/avatar")
    suspend fun setProfilePicUrl(
        @Header("Authorization") auth: String?,
        @Body avatar: UpdateUserAvatarDto
    ): Response<ResponseBody?>

    @POST("${URL_PREFIX}user/fcmtoken")
    suspend fun setFCMToken(
        @Header("Authorization") auth: String?,
        @Body token: FCMTokenDto
    ): Response<ResponseBody?>

    @POST("${URL_PREFIX}user/subscribe")
    suspend fun subscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: SubscribedClubDto
    ): Response<ResponseBody?>

    @POST("${URL_PREFIX}user/unsubscribe")
    suspend fun unsubscribeToClub(
        @Header("Authorization") auth: String?,
        @Body club: SubscribedClubDto
    ): Response<ResponseBody?>
}
