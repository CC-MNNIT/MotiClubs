package com.mnnit.moticlubs.domain.use_case.user

import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUser(private val repository: Repository) {

    operator fun invoke(userId: Long, shouldFetch: Boolean = true): Flow<Resource<User>> = repository.networkResource(
        "Error getting user",
        query = { repository.getUser(userId) ?: User() },
        apiCall = { apiService, auth -> apiService.getUserDetails(auth, userId) },
        saveResponse = { _, new -> repository.insertOrUpdateUser(new.mapToDomain()) },
        shouldFetch = shouldFetch
    )
}
