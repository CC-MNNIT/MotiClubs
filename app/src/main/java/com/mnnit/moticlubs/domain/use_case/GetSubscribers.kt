package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class GetSubscribers(private val repository: Repository) {

    operator fun invoke(clubID: Long): Flow<Resource<List<Subscriber>>> = repository.networkResource(
        "",
        query = { repository.getSubscribers(clubID) },
        apiCall = { apiService, auth -> apiService.getSubscribers(auth, clubID) },
        saveResponse = { old, new ->
            old.forEach { subscriber -> repository.deleteSubscriber(subscriber) }
            new.map { subscriberDto -> Subscriber(subscriberDto.userId, subscriberDto.clubId) }
                .forEach { subscriber -> repository.insertOrUpdateSubscriber(subscriber) }
        }
    )
}
