package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.*
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.USER_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.USER_ROUTE
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST(USER_ROUTE)
    suspend fun saveUser(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body saveUserDto: SaveUserDto?
    ): Response<UserDto?>

    @GET("$USER_ROUTE/admins")
    suspend fun getAllAdmins(@Header(AUTHORIZATION_HEADER) auth: String?): Response<List<AdminDetailDto>?>

    @GET("$USER_ROUTE/{$USER_ID_CLAIM}")
    suspend fun getUserDetails(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Path(USER_ID_CLAIM) userId: Long,
    ): Response<UserDto?>

    @POST("$USER_ROUTE/avatar")
    suspend fun setProfilePicUrl(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body avatar: UpdateUserAvatarDto,
    ): Response<UserDto?>

    @POST("$USER_ROUTE/fcm")
    suspend fun setFCMToken(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body fcm: FCMTokenDto,
    ): Response<FCMDto?>
}
