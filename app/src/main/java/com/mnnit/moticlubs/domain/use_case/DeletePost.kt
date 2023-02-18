package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeletePost(private val repository: Repository) {

    operator fun invoke(post: Post): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error sending post",
        query = { repository.getPostsFromChannel(post.channelID) },
        apiCall = { apiService, auth -> apiService.deletePost(auth, post.postID, post.channelID) },
        saveResponse = { repository.deletePost(post) }
    )
}
