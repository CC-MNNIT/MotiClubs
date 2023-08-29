package com.mnnit.moticlubs.domain.use_case.club

import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAdmins(private val repository: Repository) {

    operator fun invoke(shouldFetch: Boolean = true): Flow<Resource<List<AdminUser>>> = repository.networkResource(
        "Error getting admins",
        query = { repository.getAdmins() },
        apiCall = { apiService, auth -> apiService.getAllAdmins(auth) },
        saveResponse = { old, new ->
            old.forEach { admin -> repository.deleteAdmin(admin.userId) }

            new.map { admin -> admin.mapToDomain() }
                .forEach { user -> repository.insertOrUpdateUser(user) }

            new.map { admin -> Admin(admin.uid, admin.clubId) }
                .forEach { admin -> repository.insertOrUpdateAdmin(admin) }
        },
        shouldFetch = shouldFetch
    )
}
