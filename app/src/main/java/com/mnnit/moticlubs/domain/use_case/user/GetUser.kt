package com.mnnit.moticlubs.domain.use_case.user

import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetUser(private val repository: Repository) {

    operator fun invoke(userId: Long): Flow<Resource<User>> = repository.networkResource(
        "Error getting user",
        stampKey = ResponseStamp.USER.withKey("$userId"),
        query = { repository.getUser(userId) ?: User() },
        apiCall = { apiService, auth, stamp -> apiService.getUserDetails(auth, stamp, userId) },
        saveResponse = { _, new -> repository.insertOrUpdateUser(new.mapToDomain()) },
    )
}
