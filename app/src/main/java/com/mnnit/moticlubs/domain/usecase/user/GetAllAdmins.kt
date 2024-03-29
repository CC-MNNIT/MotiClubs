package com.mnnit.moticlubs.domain.usecase.user

import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetAllAdmins(private val repository: Repository) {

    operator fun invoke(): Flow<Resource<List<AdminUser>>> = repository.networkResource(
        "Error getting admins",
        stampKey = ResponseStamp.ADMIN,
        query = { repository.getAdmins() },
        apiCall = { apiService, auth, stamp -> apiService.getAllAdmins(auth, stamp) },
        saveResponse = { old, new ->
            new.map { admin -> admin.mapToDomain() }
                .forEach { user -> repository.insertOrUpdateUser(user) }

            old.forEach { admin -> repository.deleteAdmin(admin.userId) }

            new.forEach { admin ->
                admin.clubId
                    .map { clubId -> Admin(admin.uid, clubId) }
                    .forEach { repository.insertOrUpdateAdmin(it) }
            }
        },
    )
}
