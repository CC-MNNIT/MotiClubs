package com.mnnit.moticlubs.domain.usecase.post

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class DeletePost(private val repository: Repository) {

    operator fun invoke(post: Post, clubId: Long): Flow<Resource<Unit>> = repository.networkResource(
        "Error deleting post",
        stampKey = ResponseStamp.POST.withKey("${post.channelId}").withKey("${post.pageNo}"),
        query = { },
        apiCall = { apiService, auth, stamp -> apiService.deletePost(auth, stamp, post.postId, clubId) },
        saveResponse = { _, _ -> repository.deletePost(post) },
        remoteRequired = true,
    )
}
