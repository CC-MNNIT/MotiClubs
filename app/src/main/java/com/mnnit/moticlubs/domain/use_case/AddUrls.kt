package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.UrlDto
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddUrls(private val repository: Repository) {

    operator fun invoke(clubId: Long, list: List<UrlModel>): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error getting urls",
        query = { repository.getUrlsFromClub(clubId) },
        apiCall = { apiService, auth -> apiService.pushUrls(auth, clubId, UrlDto(list)) },
        saveResponse = { old, new ->
            old.forEach { m -> repository.deleteUrl(m) }
            new.map { m -> m.mapToDomain() }.forEach { m -> repository.insertOrUpdateUrl(m) }
        }
    )
}
