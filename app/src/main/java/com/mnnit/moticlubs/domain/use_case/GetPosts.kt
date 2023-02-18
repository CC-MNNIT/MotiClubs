package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetPosts(private val repository: Repository) {

    operator fun invoke(channelID: Long): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error getting posts",
        query = { repository.getPostsFromChannel(channelID) },
        apiCall = { apiService, auth -> apiService.getPostsFromClubChannel(auth, channelID) },
        saveResponse = {
            val list = it.map { m -> m.mapToDomain() }
            list.forEach { m -> repository.insertOrUpdatePost(m) }
        }
    )
}
