package com.mnnit.moticlubs.domain.use_case.post

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapFromDomain
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class SendPost(private val repository: Repository) {

    operator fun invoke(post: Post, clubId: Long, general: Int): Flow<Resource<List<Post>>> =
        repository.networkResource(
            "Error sending post",
            query = { repository.getPostsFromChannel(post.channelId, post.pageNo) },
            apiCall = { apiService, auth -> apiService.sendPost(auth, clubId, post.mapFromDomain(general)) },
            saveResponse = { _, new -> repository.insertOrUpdatePost(new.mapToDomain(post.pageNo)) }
        )
}
