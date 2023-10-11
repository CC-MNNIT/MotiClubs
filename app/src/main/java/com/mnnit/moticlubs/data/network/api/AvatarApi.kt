package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ClubDto
import com.mnnit.moticlubs.data.network.dto.ImageUrlDto
import com.mnnit.moticlubs.data.network.dto.UserDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.AVATAR_ROUTE
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AvatarApi {

    @POST("$AVATAR_ROUTE/user")
    @Multipart
    suspend fun updateUserAvatar(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Part file: MultipartBody.Part,
    ): Response<UserDto?>

    @POST("$AVATAR_ROUTE/club")
    @Multipart
    suspend fun updateClubAvatar(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Part file: MultipartBody.Part,
    ): Response<ClubDto?>

    @POST("$AVATAR_ROUTE/post")
    @Multipart
    suspend fun uploadPostImage(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Part file: MultipartBody.Part,
    ): Response<ImageUrlDto?>
}
