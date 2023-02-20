package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAdmins(private val repository: Repository) {

    private lateinit var cachedList: List<Admin>

    operator fun invoke(shouldFetch: Boolean = true): Flow<Resource<List<Admin>>> = repository.networkResource(
        "Error getting admins",
        query = {
            cachedList = repository.getAdmins()
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getAllAdmins(auth) },
        saveResponse = {
            cachedList.forEach { admin -> repository.deleteAdmin(admin) }

            it.map { adminDetailResponse -> adminDetailResponse.mapToDomain() }
                .forEach { user -> repository.insertOrUpdateUser(user) }

            it.map { adminDetailResponse -> Admin(adminDetailResponse.uid, adminDetailResponse.clubID) }
                .forEach { admin -> repository.insertOrUpdateAdmin(admin) }
        },
        shouldFetch = shouldFetch
    )
}
