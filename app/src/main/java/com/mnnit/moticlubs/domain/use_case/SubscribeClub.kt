package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.data.network.dto.SubscribedClubDto
import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.networkResource
import kotlinx.coroutines.flow.Flow

class SubscribeClub(private val repository: Repository) {

    operator fun invoke(subscriber: Subscriber): Flow<Resource<Unit>> = repository.networkResource(
        "Unable to subscribe club",
        query = {},
        apiCall = { apiService, auth -> apiService.subscribeToClub(auth, SubscribedClubDto(subscriber.clubID)) },
        saveResponse = { repository.insertOrUpdateSubscriber(subscriber) }
    )
}
