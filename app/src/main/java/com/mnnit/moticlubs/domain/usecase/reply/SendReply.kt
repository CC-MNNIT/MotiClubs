package com.mnnit.moticlubs.domain.usecase.reply

import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapFromDomain
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class SendReply(private val repository: Repository) {

    operator fun invoke(reply: Reply): Flow<Resource<Reply>> = repository.networkResource(
        "Error sending reply",
        stampKey = ResponseStamp.REPLY.withKey("${reply.postId}").withKey("${reply.pageNo}"),
        query = { reply },
        apiCall = { apiService, auth, stamp -> apiService.postReply(auth, stamp, reply.mapFromDomain()) },
        saveResponse = { _, new -> repository.insertOrUpdateReply(new.mapToDomain(reply.pageNo)) },
        remoteRequired = true,
    )
}
