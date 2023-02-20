package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.mapToDomain
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetClubs(private val repository: Repository) {

    private lateinit var cachedList: List<Club>

    operator fun invoke(): Flow<Resource<List<Club>>> = repository.networkResource(
        "Error getting clubs",
        query = {
            cachedList = repository.getClubs()
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getClubs(auth) },
        saveResponse = {
            cachedList.forEach { club -> repository.deleteClub(club) }
            it.map { clubModel -> clubModel.mapToDomain() }
                .forEach { club -> repository.insertOrUpdateClub(club) }
        }
    )
}
