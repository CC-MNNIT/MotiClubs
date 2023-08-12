package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeletePost(private val repository: Repository) {

    operator fun invoke(post: Post, clubId: Long): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error deleting post",
        query = { repository.getPostsFromChannel(post.channelId, post.pageNo) },
        apiCall = { apiService, auth -> apiService.deletePost(auth, post.postId, clubId) },
        saveResponse = { _, _ -> repository.deletePost(post) }
    )
}
