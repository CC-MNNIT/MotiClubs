package com.mnnit.moticlubs.domain.use_case.subscribe

import com.mnnit.moticlubs.data.network.dto.SubscribedClubDto
import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class UnsubscribeClub(private val repository: Repository) {

    operator fun invoke(subscriber: Subscriber): Flow<Resource<Unit>> = repository.networkResource(
        "Unable to unsubscribe club",
        query = {},
        apiCall = { apiService, auth -> apiService.unsubscribeToClub(auth, SubscribedClubDto(subscriber.clubId)) },
        saveResponse = { _, _ -> repository.deleteSubscriber(subscriber) }
    )
}
