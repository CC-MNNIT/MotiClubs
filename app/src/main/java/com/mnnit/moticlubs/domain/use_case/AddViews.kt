package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.ViewDto
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class AddViews(private val repository: Repository) {

    operator fun invoke(view: View): Flow<Resource<List<View>>> = repository.networkResource(
        "Unable to get views",
        query = { repository.getViewsFromPost(view.postID) },
        apiCall = { apiService, auth -> apiService.addView(auth, ViewDto(view.postID, view.userID)) },
        saveResponse = { repository.insertOrUpdateView(view) }
    )
}
