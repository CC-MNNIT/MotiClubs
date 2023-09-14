package com.mnnit.moticlubs.data.network.api

import com.mnnit.moticlubs.data.network.dto.GithubContributorDto
import retrofit2.Response
import retrofit2.http.GET

interface GithubApi {

    @GET("https://api.github.com/repos/CC-MNNIT/MotiClubs/contributors")
    suspend fun getAppContributors(): Response<List<GithubContributorDto>?>

    @GET("https://api.github.com/repos/CC-MNNIT/MotiClubs-Service/contributors")
    suspend fun getBackendContributors(): Response<List<GithubContributorDto>?>
}
