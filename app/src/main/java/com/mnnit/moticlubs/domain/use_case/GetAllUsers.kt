package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAllUsers(private val repository: Repository) {

    private lateinit var cachedList: List<User>

    operator fun invoke(shouldFetch: Boolean = true): Flow<Resource<List<User>>> = repository.networkResource(
        "Error getting user",
        query = {
            cachedList = repository.getAllUsers()
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getAllUsers(auth) },
        saveResponse = { list ->
            cachedList.forEach { cached -> repository.deleteUser(cached) }
            list.map { it.mapToDomain() }.forEach { user -> repository.insertOrUpdateUser(user) }
        },
        shouldFetch = shouldFetch
    )
}
