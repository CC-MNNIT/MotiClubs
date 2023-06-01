package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUser(private val repository: Repository) {

    operator fun invoke(userID: Long, shouldFetch: Boolean = true): Flow<Resource<User>> = repository.networkResource(
        "Error getting user",
        query = { repository.getUser(userID) ?: User() },
        apiCall = { apiService, auth -> apiService.getUserDetails(auth, userID) },
        saveResponse = { repository.insertOrUpdateUser(it.mapToDomain()) },
        shouldFetch = shouldFetch
    )
}
