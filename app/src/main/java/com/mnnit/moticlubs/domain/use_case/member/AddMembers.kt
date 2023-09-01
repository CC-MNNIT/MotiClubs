package com.mnnit.moticlubs.domain.use_case.member

import com.mnnit.moticlubs.data.network.dto.AddMemberDto
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddMembers(private val repository: Repository) {

    operator fun invoke(
        clubId: Long,
        channelId: Long,
        userIdList: List<Long>
    ): Flow<Resource<List<Member>>> = repository.networkResource(
        "",
        stampKey = ResponseStamp.MEMBER.withKey("$channelId"),
        query = { repository.getMembers(-1L) },
        apiCall = { apiService, auth, stamp ->
            apiService.addMembers(
                auth,
                stamp,
                AddMemberDto(clubId, channelId, userIdList)
            )
        },
        saveResponse = { _, new ->
            new.forEach { member -> repository.insertOrUpdateMember(Member(member.userId, member.channelId)) }
        }
    )
}
