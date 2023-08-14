package com.mnnit.moticlubs.domain.use_case.reply

import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource

class GetReplies(private val repository: Repository) {

    operator fun invoke(postID: Long, page: Int) = repository.networkResource(
        errorMsg = "Unable to get replies",
        query = { repository.getRepliesByPost(postID, page) },
        apiCall = { apiService, auth -> apiService.getReplies(auth, postID, page) },
        saveResponse = { old, new ->
            old.forEach { reply -> repository.deleteReply(reply) }
            new.map { dto -> dto.mapToDomain(page) }
                .forEach { reply -> repository.insertOrUpdateReply(reply) }
        }
    )
}
