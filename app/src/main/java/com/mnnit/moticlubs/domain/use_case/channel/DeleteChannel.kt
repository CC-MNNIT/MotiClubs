package com.mnnit.moticlubs.domain.use_case.channel

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeleteChannel(private val repository: Repository) {

    operator fun invoke(channel: Channel): Flow<Resource<Unit>> = repository.networkResource(
        "Error deleting channel",
        stampKey = ResponseStamp.CHANNEL,
        query = {},
        apiCall = { apiService, auth, stamp ->
            apiService.deleteChannel(
                auth,
                stamp,
                channel.channelId,
                channel.clubId
            )
        },
        saveResponse = { _, _ -> repository.deleteChannel(channel) },
        remoteRequired = true,
    )
}
