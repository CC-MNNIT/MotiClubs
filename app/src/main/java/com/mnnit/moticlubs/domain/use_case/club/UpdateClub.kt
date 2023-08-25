package com.mnnit.moticlubs.domain.use_case.club

import com.mnnit.moticlubs.data.network.dto.UpdateClubDto
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UpdateClub(private val repository: Repository) {

    operator fun invoke(club: Club): Flow<Resource<Club>> = repository.networkResource(
        "Error updating club",
        query = { repository.getClub(clubID = club.clubId) },
        apiCall = { apiService, auth ->
            apiService.updateClub(
                auth,
                club.clubId,
                UpdateClubDto(
                    description = club.description,
                    avatar = club.avatar,
                    summary = club.summary
                )
            )
        },
        saveResponse = { _, new -> repository.insertOrUpdateClub(new.mapToDomain()) }
    )
}
