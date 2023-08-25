package com.mnnit.moticlubs.data.data_source

import androidx.room.*
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
