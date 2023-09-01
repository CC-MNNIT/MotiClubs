package com.mnnit.moticlubs.data.data_source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mnnit.moticlubs.domain.model.Stamp

@Dao
interface StampDao {

    @Upsert
    suspend fun insertOrUpdateStamp(stamp: Stamp)

    @Query("SELECT * FROM stamp where stamp.header = :key")
    suspend fun getStampByKey(key: String): Stamp?
}
