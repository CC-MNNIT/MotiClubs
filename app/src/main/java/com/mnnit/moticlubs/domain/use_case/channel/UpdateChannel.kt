package com.mnnit.moticlubs.domain.use_case.channel

import com.mnnit.moticlubs.data.network.dto.UpdateChannelDto
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdateChannel(private val repository: Repository) {

    operator fun invoke(channel: Channel): Flow<Resource<Channel>> = repository.networkResource(
        "Unable to update channel",
        query = { channel },
        apiCall = { apiService, auth ->
            apiService.updateChannel(
                auth,
                channel.channelId,
                UpdateChannelDto(channel.clubId, channel.name, channel.private == 1)
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateChannel(new.mapToDomain()) }
    )
}
