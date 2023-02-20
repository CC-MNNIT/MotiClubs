package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetChannels(private val repository: Repository) {

    private lateinit var cachedList: List<Channel>

    operator fun invoke(): Flow<Resource<List<Channel>>> = repository.networkResource(
        "Error getting channels",
        query = {
            cachedList = repository.getChannels()
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getAllChannels(auth) },
        saveResponse = {
            cachedList.forEach { channel -> repository.deleteChannel(channel) }
            it.map { channelDto -> channelDto.mapToDomain() }
                .forEach { channel -> repository.insertOrUpdateChannel(channel) }
        }
    )
}
