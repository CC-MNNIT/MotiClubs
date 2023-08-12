package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.UpdateUserAvatarDto
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdateUser(private val repository: Repository) {

    operator fun invoke(user: User): Flow<Resource<User>> = repository.networkResource(
        "Unable to update user",
        query = { user },
        apiCall = { apiService, auth -> apiService.setProfilePicUrl(auth, UpdateUserAvatarDto(user.avatar)) },
        saveResponse = {_, new -> repository.insertOrUpdateUser(new.mapToDomain()) }
    )
}
