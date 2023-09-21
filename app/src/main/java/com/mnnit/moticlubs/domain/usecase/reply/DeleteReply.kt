package com.mnnit.moticlubs.domain.usecase.reply

import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeleteReply(private val repository: Repository) {

    operator fun invoke(reply: Reply): Flow<Resource<Unit>> = repository.networkResource(
        "Error deleting reply",
        stampKey = ResponseStamp.REPLY.withKey("${reply.postId}").withKey("${reply.pageNo}"),
        query = { },
        apiCall = { apiService, auth, stamp -> apiService.deleteReply(auth, stamp, reply.time) },
        saveResponse = { _, _ -> repository.deleteReply(reply) },
        remoteRequired = true,
    )
}
