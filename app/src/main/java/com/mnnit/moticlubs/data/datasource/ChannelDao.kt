package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Channel

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChannel(channel: Channel)

    @Query("SELECT * FROM channel where channel.chid = :channelId")
    suspend fun getChannel(channelId: Long): Channel

    @Query("SELECT * FROM channel")
    suspend fun getAllChannels(): List<Channel>

    @Delete
    suspend fun deleteChannel(channel: Channel)
}
