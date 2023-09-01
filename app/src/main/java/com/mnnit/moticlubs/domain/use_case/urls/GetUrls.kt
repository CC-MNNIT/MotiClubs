package com.mnnit.moticlubs.domain.use_case.urls

import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUrls(private val repository: Repository) {

    operator fun invoke(clubId: Long): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error getting urls",
        stampKey = ResponseStamp.URL.withKey("$clubId"),
        query = { repository.getUrlsFromClub(clubId) },
        apiCall = { apiService, auth, stamp -> apiService.getUrls(auth, stamp, clubId) },
        saveResponse = { old, new ->
            old.forEach { url -> repository.deleteUrl(url) }
            new.map { urlResponseModel -> urlResponseModel.mapToDomain() }
                .forEach { url -> repository.insertOrUpdateUrl(url) }
        }
    )
}
