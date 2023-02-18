package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAdmins(private val repository: Repository) {

    operator fun invoke(shouldFetch: Boolean = true): Flow<Resource<List<Admin>>> = repository.networkResource(
        "Error getting admins",
        query = { repository.getAdmins() },
        apiCall = { apiService, auth -> apiService.getAllAdmins(auth) },
        saveResponse = {
            val maps = it.map { m -> m.mapToDomain() }
            maps.forEach { m -> repository.insertOrUpdateUser(m) }

            val admins = it.map { m -> Admin(m.uid, m.clubID) }
            admins.forEach { m -> repository.insertOrUpdateAdmin(m) }
        },
        shouldFetch = shouldFetch
    )
}
