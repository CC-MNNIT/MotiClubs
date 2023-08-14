package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.mapFromDomain
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource

class SendReply(private val repository: Repository) {

    operator fun invoke(reply: Reply) = repository.networkResource(
        "Error sending reply",
        query = { repository.getRepliesByPost(reply.postId, reply.pageNo) },
        apiCall = { apiService, auth -> apiService.postReply(auth, reply.mapFromDomain()) },
        saveResponse = { _, new -> repository.insertOrUpdateReply(new.mapToDomain(reply.pageNo)) }
    )
}
