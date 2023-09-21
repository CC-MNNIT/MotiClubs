package com.mnnit.moticlubs.domain.usecase.channel

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAllChannels(private val repository: Repository) {

    operator fun invoke(userId: Long): Flow<Resource<List<Channel>>> = repository.networkResource(
        "Error getting channels",
        stampKey = ResponseStamp.CHANNEL,
        query = { repository.getAllChannels(userId) },
        apiCall = { apiService, auth, stamp -> apiService.getAllChannels(auth, stamp) },
        saveResponse = { old, new ->
            old.forEach { channel ->
                repository.deleteChannel(channel)

                if (channel.private == 1) {
                    repository.deleteMember(Member(userId = userId, channelId = channel.channelId))
                }
            }

            new.map { channelDto -> channelDto.mapToDomain() }
                .forEach { channel ->
                    repository.insertOrUpdateChannel(channel)

                    if (channel.private == 1) {
                        repository.insertOrUpdateMember(Member(userId = userId, channelId = channel.channelId))
                    }
                }
        },
    )
}
