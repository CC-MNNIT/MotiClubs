package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource

class GetReplies(private val repository: Repository) {

    operator fun invoke(postID: Long) = repository.networkResource(
        errorMsg = "Unable to get replies",
        query = { repository.getRepliesByPost(postID) },
        apiCall = { apiService, auth -> apiService.getReplies(auth, postID) },
        saveResponse = { old, new ->
            old.forEach { reply -> repository.deleteReply(reply) }
            new.map { dto -> dto.mapToDomain() }
                .forEach { reply -> repository.insertOrUpdateReply(reply) }
        }
    )
}
