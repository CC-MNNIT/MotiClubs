package com.mnnit.moticlubs.domain.use_case.user

import com.mnnit.moticlubs.data.network.dto.UpdateUserAvatarDto
import com.mnnit.moticlubs.data.network.dto.UpdateUserContactDto
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdateUser(private val repository: Repository) {

    operator fun invoke(user: User): Flow<Resource<User>> = repository.networkResource(
        "Unable to update user",
        stampKey = ResponseStamp.USER,
        query = { user },
        apiCall = { apiService, auth, stamp ->
            apiService.setProfilePicUrl(
                auth,
                stamp,
                UpdateUserAvatarDto(user.avatar)
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateUser(new.mapToDomain()) },
        remoteRequired = true,
    )

    operator fun invoke(user: User, contact: String): Flow<Resource<User>> = repository.networkResource(
        "Unable to update user contact",
        stampKey = ResponseStamp.USER,
        query = { user },
        apiCall = { apiService, auth, stamp ->
            apiService.setContact(
                auth,
                stamp,
                UpdateUserContactDto(contact)
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateUser(new.mapToDomain()) },
        remoteRequired = true,
    )
}
