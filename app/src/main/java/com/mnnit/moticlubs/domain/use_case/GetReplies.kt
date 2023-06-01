package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource

class GetReplies(private val repository: Repository) {

    private lateinit var cachedList: List<Reply>

    operator fun invoke(postID: Long) = repository.networkResource(
        errorMsg = "Unable to get replies",
        query = {
            cachedList = repository.getRepliesByPost(postID)
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getReplies(auth, postID) },
        saveResponse = {
            cachedList.forEach { reply -> repository.deleteReply(reply) }
            it.map { dto -> dto.mapToDomain() }
                .forEach { reply -> repository.insertOrUpdateReply(reply) }
        }
    )
}
