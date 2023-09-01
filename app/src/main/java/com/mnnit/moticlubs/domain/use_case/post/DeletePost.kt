package com.mnnit.moticlubs.domain.use_case.post

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeletePost(private val repository: Repository) {

    operator fun invoke(post: Post, clubId: Long): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error deleting post",
        stampKey = ResponseStamp.POST.withKey("${post.channelId}").withKey("${post.pageNo}"),
        query = { repository.getPostsFromChannel(post.channelId, post.pageNo) },
        apiCall = { apiService, auth, stamp -> apiService.deletePost(auth, post.postId, clubId, stamp) },
        saveResponse = { _, _ -> repository.deletePost(post) }
    )
}
