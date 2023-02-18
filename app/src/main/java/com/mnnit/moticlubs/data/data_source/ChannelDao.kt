package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.Channel

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChannel(channel: Channel)

    @Query("SELECT * FROM channel")
    suspend fun getChannels(): List<Channel>

    @Delete
    suspend fun deleteChannel(channel: Channel)
}
