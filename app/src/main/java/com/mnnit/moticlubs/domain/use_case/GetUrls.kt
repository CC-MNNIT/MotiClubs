package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUrls(private val repository: Repository) {

    operator fun invoke(clubID: Int): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error getting urls",
        query = { repository.getUrlsFromClub(clubID) },
        apiCall = { apiService, auth -> apiService.getUrls(auth, clubID) },
        saveResponse = {
            it.map { m -> m.mapToDomain() }.forEach { m -> repository.insertOrUpdateUrl(m) }
        }
    )
}
