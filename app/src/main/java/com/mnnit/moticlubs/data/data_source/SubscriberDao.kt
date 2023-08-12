package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.Subscriber

@Dao
interface SubscriberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSubscriber(subscriber: Subscriber)

    @Query("SELECT * FROM subscriber WHERE subscriber.cid = :clubID")
    suspend fun getSubscribers(clubID: Long): List<Subscriber>

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber)
}
