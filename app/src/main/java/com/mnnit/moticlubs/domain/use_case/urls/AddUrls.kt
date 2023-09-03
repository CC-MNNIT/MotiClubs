package com.mnnit.moticlubs.domain.use_case.urls

import com.mnnit.moticlubs.data.network.dto.UrlDto
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddUrls(private val repository: Repository) {

    operator fun invoke(clubId: Long, list: List<UrlModel>): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error updating urls",
        stampKey = ResponseStamp.URL.withKey("$clubId"),
        query = { repository.getUrlsFromClub(clubId) },
        apiCall = { apiService, auth, stamp -> apiService.pushUrls(auth, stamp, clubId, UrlDto(list)) },
        saveResponse = { old, new ->
            old.forEach { m -> repository.deleteUrl(m) }
            new.map { m -> m.mapToDomain() }.forEach { m -> repository.insertOrUpdateUrl(m) }
        },
        remoteRequired = true,
    )
}
