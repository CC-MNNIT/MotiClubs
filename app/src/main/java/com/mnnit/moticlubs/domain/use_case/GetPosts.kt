package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetPosts(private val repository: Repository) {

    private lateinit var cachedList: List<Post>

    operator fun invoke(channelID: Long): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error getting posts",
        query = {
            cachedList = repository.getPostsFromChannel(channelID)
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getPostsFromClubChannel(auth, channelID) },
        saveResponse = {
            cachedList.forEach { post -> repository.deletePost(post) }
            it.map { postDto -> postDto.mapToDomain() }
                .forEach { post -> repository.insertOrUpdatePost(post) }
        }
    )
}
