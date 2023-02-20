package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetSubscribers(private val repository: Repository) {

    private lateinit var cachedList: List<Subscriber>

    operator fun invoke(clubID: Int): Flow<Resource<List<Subscriber>>> = repository.networkResource(
        "",
        query = {
            cachedList = repository.getSubscribers(clubID)
            cachedList
        },
        apiCall = { apiService, auth -> apiService.getSubscribers(auth, clubID) },
        saveResponse = {
            cachedList.forEach { subscriber -> repository.deleteSubscriber(subscriber) }
            it.map { subscriberDto -> Subscriber(subscriberDto.userID, subscriberDto.clubID) }
                .forEach { subscriber -> repository.insertOrUpdateSubscriber(subscriber) }
        }
    )
}
