package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.AdminDetailDto
import com.mnnit.moticlubs.data.network.dto.FCMDto
import com.mnnit.moticlubs.data.network.dto.FCMTokenDto
import com.mnnit.moticlubs.data.network.dto.SaveUserDto
import com.mnnit.moticlubs.data.network.dto.UpdateUserContactDto
import com.mnnit.moticlubs.data.network.dto.UserDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.STAMP_HEADER
import com.mnnit.moticlubs.domain.util.Constants.USER_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.USER_ROUTE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @POST(USER_ROUTE)
    suspend fun saveUser(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Body saveUserDto: SaveUserDto?,
    ): Response<UserDto?>

    @GET("$USER_ROUTE/admins")
    suspend fun getAllAdmins(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
    ): Response<List<AdminDetailDto>?>

    @GET("$USER_ROUTE/all")
    suspend fun getAllUsers(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
    ): Response<List<UserDto>?>

    @GET("$USER_ROUTE/{$USER_ID_CLAIM}")
    suspend fun getUserDetails(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Path(USER_ID_CLAIM) userId: Long,
    ): Response<UserDto?>

    @PUT("$USER_ROUTE/contact")
    suspend fun setContact(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Header(STAMP_HEADER) stamp: Long,
        @Body contact: UpdateUserContactDto,
    ): Response<UserDto?>

    @PUT("$USER_ROUTE/fcm")
    suspend fun setFCMToken(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body fcm: FCMTokenDto,
    ): Response<FCMDto?>
}
