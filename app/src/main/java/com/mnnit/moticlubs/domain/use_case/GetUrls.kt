package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUrls(private val repository: Repository) {

    private lateinit var cachedList: List<Url>

    operator fun invoke(clubID: Int): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error getting urls",
        query = {
            cachedList = repository.getUrlsFromClub(clubID)
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getUrls(auth, clubID) },
        saveResponse = {
            cachedList.forEach { url -> repository.deleteUrl(url) }
            it.map { urlResponseModel -> urlResponseModel.mapToDomain() }
                .forEach { url -> repository.insertOrUpdateUrl(url) }
        }
    )
}
