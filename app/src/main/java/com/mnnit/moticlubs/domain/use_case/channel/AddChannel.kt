package com.mnnit.moticlubs.domain.use_case.channel

import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddChannel(private val repository: Repository) {

    operator fun invoke(channel: Channel): Flow<Resource<Channel>> = repository.networkResource(
        "Unable to create channel",
        query = { channel },
        apiCall = { apiService, auth ->
            apiService.createChannel(auth, ChannelDto(channel.channelId, channel.clubId, channel.name))
        },
        saveResponse = {_, new -> repository.insertOrUpdateChannel(new.mapToDomain()) }
    )
}