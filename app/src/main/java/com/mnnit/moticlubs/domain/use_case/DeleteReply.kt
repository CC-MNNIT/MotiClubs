package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.networkResource

class DeleteReply(private val repository: Repository) {

    operator fun invoke(reply: Reply) = repository.networkResource(
        "Error deleting reply",
        query = { repository.getRepliesByPost(reply.postId, reply.pageNo) },
        apiCall = { apiService, auth -> apiService.deleteReply(auth, reply.time) },
        saveResponse = { _, _ -> repository.deleteReply(reply) }
    )
}
