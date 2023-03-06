package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapFromDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class SendPost(private val repository: Repository) {

    operator fun invoke(post: Post, clubID: Int, general: Int): Flow<Resource<List<Post>>> = repository.networkResource(
        "Error sending post",
        query = { repository.getPostsFromChannel(post.channelID, post.pageNo) },
        apiCall = { apiService, auth -> apiService.sendPost(auth, post.mapFromDomain(clubID, general)) },
        saveResponse = { repository.insertOrUpdatePost(post) }
    )
}
