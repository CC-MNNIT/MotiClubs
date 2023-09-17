package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Url

@Dao
interface UrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUrl(url: Url)

    @Query("SELECT * FROM url WHERE url.cid = :clubId")
    suspend fun getUrlsFromClub(clubId: Long): List<Url>

    @Delete
    suspend fun deleteUrl(url: Url)
}
