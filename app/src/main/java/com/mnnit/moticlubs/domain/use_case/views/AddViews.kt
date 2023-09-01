package com.mnnit.moticlubs.domain.use_case.views

import com.mnnit.moticlubs.data.network.dto.ViewDto
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddViews(private val repository: Repository) {

    operator fun invoke(view: View): Flow<Resource<List<View>>> = repository.networkResource(
        "Unable to get views",
        stampKey = ResponseStamp.NONE,
        query = { repository.getViewsFromPost(view.postId) },
        apiCall = { apiService, auth, _ -> apiService.addView(auth, ViewDto(view.postId, view.userId)) },
        saveResponse = { _, new -> repository.insertOrUpdateView(new.mapToDomain()) }
    )
}
