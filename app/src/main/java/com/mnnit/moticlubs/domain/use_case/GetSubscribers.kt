package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetSubscribers(private val repository: Repository) {

    operator fun invoke(clubID: Int): Flow<Resource<List<Subscriber>>> = repository.networkResource(
        "",
        query = { repository.getSubscribers(clubID) },
        apiCall = { apiService, auth -> apiService.getSubscribers(auth, clubID) },
        saveResponse = {
            it.map { m -> Subscriber(m.userID, m.clubID) }.forEach { m -> repository.insertOrUpdateSubscriber(m) }
        }
    )
}
