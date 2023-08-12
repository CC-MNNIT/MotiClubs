package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeleteChannel(private val repository: Repository) {

    operator fun invoke(channel: Channel): Flow<Resource<Unit>> = repository.networkResource(
        "Error deleting channel",
        query = {},
        apiCall = { apiService, auth -> apiService.deleteChannel(auth, channel.channelId, channel.clubId) },
        saveResponse = { _, _ -> repository.deleteChannel(channel) }
    )
}
