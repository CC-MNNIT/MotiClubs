package com.mnnit.moticlubs.domain.usecase.user

import com.mnnit.moticlubs.data.network.dto.UpdateUserContactDto
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UpdateUser(private val repository: Repository) {

    operator fun invoke(userId: Long, file: File): Flow<Resource<User>> = repository.networkResource(
        "Unable to update user",
        stampKey = object : ResponseStamp.StampKey("UserAvatar") {},
        query = { repository.getUser(userId) ?: User() },
        apiCall = { apiService, auth, _ ->
            apiService.updateUserAvatar(
                auth,
//                stamp,
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody()),
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
                UpdateUserContactDto(contact),
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateUser(new.mapToDomain()) },
        remoteRequired = true,
    )
}
