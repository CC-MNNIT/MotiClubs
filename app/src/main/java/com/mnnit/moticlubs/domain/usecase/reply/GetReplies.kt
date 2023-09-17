package com.mnnit.moticlubs.domain.usecase.reply

import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource

class GetReplies(private val repository: Repository) {

    operator fun invoke(postId: Long, page: Int) = repository.networkResource(
        errorMsg = "Unable to get replies",
        stampKey = ResponseStamp.REPLY.withKey("$postId").withKey("$page"),
        query = { repository.getRepliesByPost(postId, page) },
        apiCall = { apiService, auth, stamp -> apiService.getReplies(auth, stamp, postId, page) },
        saveResponse = { old, new ->
            old.forEach { reply -> repository.deleteReply(reply) }
            new.map { dto -> dto.mapToDomain(page) }
                .forEach { reply -> repository.insertOrUpdateReply(reply) }
        },
    )
}
