package com.mnnit.moticlubs.domain.use_case.channel

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapFromDomain
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddChannel(private val repository: Repository) {

    operator fun invoke(channel: Channel): Flow<Resource<Channel>> = repository.networkResource(
        "Unable to create channel",
        stampKey = ResponseStamp.CHANNEL,
        query = { channel },
        apiCall = { apiService, auth, stamp -> apiService.createChannel(auth, stamp, channel.mapFromDomain()) },
        saveResponse = { _, new -> repository.insertOrUpdateChannel(new.mapToDomain()) }
    )
}
