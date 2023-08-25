package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.ViewDto
import com.mnnit.moticlubs.domain.util.Constants.AUTHORIZATION_HEADER
import com.mnnit.moticlubs.domain.util.Constants.POST_ID_CLAIM
import com.mnnit.moticlubs.domain.util.Constants.VIEW_ROUTE
import retrofit2.Response
import retrofit2.http.*

interface ViewsApi {

    @GET(VIEW_ROUTE)
    suspend fun getViews(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Query(POST_ID_CLAIM) postId: Long,
    ): Response<List<ViewDto>?>

    @POST(VIEW_ROUTE)
    suspend fun addView(
        @Header(AUTHORIZATION_HEADER) auth: String?,
        @Body viewDto: ViewDto,
    ): Response<ViewDto?>
}
