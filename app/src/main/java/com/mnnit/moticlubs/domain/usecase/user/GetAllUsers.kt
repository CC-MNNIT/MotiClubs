package com.mnnit.moticlubs.domain.usecase.user

import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAllUsers(private val repository: Repository) {

    operator fun invoke(): Flow<Resource<List<User>>> = repository.networkResource(
        "Error getting all users",
        stampKey = ResponseStamp.USER.withKey("all"),
        query = { repository.getAllUsers() },
        apiCall = { apiService, auth, stamp -> apiService.getAllUsers(auth, stamp) },
        saveResponse = { _, new ->
            new.map { user -> user.mapToDomain() }
                .forEach { user -> repository.insertOrUpdateUser(user) }
        },
    )
}
