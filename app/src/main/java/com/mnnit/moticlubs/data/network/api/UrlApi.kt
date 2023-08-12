package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.UrlDto
import com.mnnit.moticlubs.data.network.dto.UrlResponseModel
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.CLUB_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.URL_ROUTE
import retrofit2.Response
import retrofit2.http.*

interface UrlApi {

    @GET(URL_ROUTE)
    suspend fun getUrls(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CLUB_ID_CLAIM) clubId: Long
    ): Response<List<UrlResponseModel>?>

    @POST(URL_ROUTE)
    suspend fun pushUrls(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(CLUB_ID_CLAIM) clubId: Long,
        @Body urlDto: UrlDto
    ): Response<List<UrlResponseModel>?>
}
