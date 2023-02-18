package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.UrlDto
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddUrls(private val repository: Repository) {

    operator fun invoke(clubID: Int, list: List<UrlModel>): Flow<Resource<List<Url>>> = repository.networkResource(
        "Error getting urls",
        query = { repository.getUrlsFromClub(clubID) },
        apiCall = { apiService, auth -> apiService.pushUrls(auth, clubID, UrlDto(list)) },
        saveResponse = {
            repository.getUrlsFromClub(clubID).forEach { m -> repository.deleteUrl(m) }
            list.map { m -> Url(m.urlID, clubID, m.name, m.color, m.url) }
                .forEach { m -> repository.insertOrUpdateUrl(m) }
        }
    )
}
