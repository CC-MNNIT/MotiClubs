package com.mnnit.moticlubs.domain.use_case.post

import com.mnnit.moticlubs.data.network.dto.UpdatePostModel
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdatePost(private val repository: Repository) {

    operator fun invoke(post: Post, clubId: Long): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error updating post",
        stampKey = ResponseStamp.POST.withKey("${post.channelId}").withKey("${post.pageNo}"),
        query = { repository.getPostsFromChannel(post.channelId, post.pageNo) },
        apiCall = { apiService, auth, stamp ->
            apiService.updatePost(
                auth,
                stamp,
                post.postId,
                clubId,
                UpdatePostModel(post.message)
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdatePost(new.mapToDomain(post.pageNo)) },
        remoteRequired = true,
    )
}
