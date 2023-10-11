package com.mnnit.moticlubs.domain.usecase.club

import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UpdateClub(private val repository: Repository) {

    operator fun invoke(club: Club): Flow<Resource<Club>> = repository.networkResource(
        "Error updating club",
        stampKey = ResponseStamp.CLUB,
        query = { repository.getClub(clubID = club.clubId) },
        apiCall = { apiService, auth, stamp ->
            apiService.updateClub(
                auth,
                stamp,
                club.clubId,
                UpdateClubDto(
                    description = club.description,
                    summary = club.summary,
                ),
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateClub(new.mapToDomain()) },
        remoteRequired = true,
    )

    operator fun invoke(clubId: Long, file: File): Flow<Resource<Club>> = repository.networkResource(
        "Error updating club",
        stampKey = ResponseStamp.CLUB,
        query = { repository.getClub(clubID = clubId) },
        apiCall = { apiService, auth, _ ->
            apiService.updateClubAvatar(
                auth,
                clubId,
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody()),
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateClub(new.mapToDomain()) },
        remoteRequired = true,
    )
}
