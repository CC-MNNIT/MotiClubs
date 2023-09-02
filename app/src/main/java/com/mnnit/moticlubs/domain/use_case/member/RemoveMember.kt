package com.mnnit.moticlubs.domain.use_case.member

import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class RemoveMember(private val repository: Repository) {

    operator fun invoke(
        clubId: Long,
        channelId: Long,
        userId: Long,
    ): Flow<Resource<Unit>> = repository.networkResource(
        "",
        stampKey = ResponseStamp.MEMBER.withKey("$channelId"),
        query = { },
        apiCall = { apiService, auth, stamp ->
            apiService.removeMember(auth, stamp, clubId, channelId, userId)
        },
        saveResponse = { _, _ ->
            repository.deleteMember(Member(userId, channelId))
        }
    )
}
