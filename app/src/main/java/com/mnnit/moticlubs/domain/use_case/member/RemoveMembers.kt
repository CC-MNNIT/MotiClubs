package com.mnnit.moticlubs.domain.use_case.member

import com.mnnit.moticlubs.data.network.dto.AddMemberDto
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class RemoveMembers(private val repository: Repository) {

    operator fun invoke(
        clubId: Long,
        channelId: Long,
        userIdList: List<Long>
    ): Flow<Resource<Unit>> = repository.networkResource(
        "",
        query = { },
        apiCall = { apiService, auth -> apiService.removeMembers(auth, AddMemberDto(clubId, channelId, userIdList)) },
        saveResponse = { _, _ ->
            userIdList.forEach { uid -> repository.deleteMember(Member(uid, channelId)) }
        }
    )
}
