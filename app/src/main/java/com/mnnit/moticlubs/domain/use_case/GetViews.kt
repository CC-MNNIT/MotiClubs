package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetViews(private val repository: Repository) {

    operator fun invoke(postID: Long): Flow<Resource<List<View>>> = repository.networkResource(
        "Unable to get views",
        query = { repository.getViewsFromPost(postID) },
        apiCall = { apiService, auth -> apiService.getViews(auth, postID) },
        saveResponse = { _, new -> new.map { m -> m.mapToDomain() }.forEach { m -> repository.insertOrUpdateView(m) } }
    )
}
