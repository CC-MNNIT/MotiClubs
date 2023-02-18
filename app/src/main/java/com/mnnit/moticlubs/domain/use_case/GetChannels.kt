package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetChannels(private val repository: Repository) {

    operator fun invoke(): Flow<Resource<List<Channel>>> = repository.networkResource(
        "Error getting channels",
        query = { repository.getChannels() },
        apiCall = { apiService, auth -> apiService.getAllChannels(auth) },
        saveResponse = {
            val list = it.map { m -> m.mapToDomain() }
            list.forEach { m -> repository.insertOrUpdateChannel(m) }
        }
    )
}
