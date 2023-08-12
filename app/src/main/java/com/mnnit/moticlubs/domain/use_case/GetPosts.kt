package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetPosts(private val repository: Repository) {

    operator fun invoke(channelID: Long, page: Int): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error getting posts",
        query = { repository.getPostsFromChannel(channelID, page) },
        apiCall = { apiService, auth -> apiService.getPostsFromChannel(auth, channelID, page) },
        saveResponse = { old, new ->
            old.forEach { post -> repository.deletePost(post) }
            new.map { postDto -> postDto.mapToDomain(page) }
                .forEach { post -> repository.insertOrUpdatePost(post) }
        }
    )
}
