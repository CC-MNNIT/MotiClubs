package com.mnnit.moticlubs.domain.use_case.channel

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetChannel(private val repository: Repository) {

    operator fun invoke(channelId: Long): Flow<Resource<Channel>> = repository.networkResource(
        "Error getting channels",
        query = { repository.getChannel(channelId) },
        apiCall = { apiService, auth -> apiService.getChannel(auth, channelId) },
        saveResponse = { old, new ->
            repository.deleteChannel(old)
            repository.insertOrUpdateChannel(new.mapToDomain())
        }
    )
}
