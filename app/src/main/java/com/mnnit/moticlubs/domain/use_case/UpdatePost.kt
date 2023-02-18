package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.UpdatePostModel
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdatePost(private val repository: Repository) {

    operator fun invoke(post: Post): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error sending post",
        query = { repository.getPostsFromChannel(post.channelID) },
        apiCall = { apiService, auth -> apiService.updatePost(auth, post.postID, UpdatePostModel(post.message)) },
        saveResponse = { repository.insertOrUpdatePost(post) }
    )
}
