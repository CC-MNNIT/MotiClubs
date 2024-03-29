package com.mnnit.moticlubs.domain.usecase.member

import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetMembers(private val repository: Repository) {

    operator fun invoke(channelId: Long): Flow<Resource<List<Member>>> = repository.networkResource(
        "",
        stampKey = ResponseStamp.MEMBER.withKey("$channelId"),
        query = { repository.getMembers(channelId) },
        apiCall = { apiService, auth, stamp -> apiService.getMembers(auth, stamp, channelId) },
        saveResponse = { old, new ->
            old.forEach { member -> repository.deleteMember(member) }
            new.map { memberDto -> Member(memberDto.userId, memberDto.channelId) }
                .forEach { member -> repository.insertOrUpdateMember(member) }
        },
    )
}
