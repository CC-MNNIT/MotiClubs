package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetClubs(private val repository: Repository) {

    operator fun invoke(): Flow<Resource<List<Club>>> = repository.networkResource(
        "Error getting clubs",
        query = { repository.getClubs() },
        apiCall = { apiService, auth -> apiService.getClubs(auth) },
        saveResponse = { old, new ->
            old.forEach { club -> repository.deleteClub(club) }
            new.map { clubModel -> clubModel.mapToDomain() }
                .forEach { club -> repository.insertOrUpdateClub(club) }
        }
    )
}
