package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Club

@Dao
interface ClubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateClub(club: Club)

    @Query("SELECT * FROM club")
    suspend fun getClubs(): List<Club>

    @Query("SELECT * FROM club WHERE club.cid = :clubId")
    suspend fun getClub(clubId: Long): Club

    @Delete
    suspend fun deleteClub(club: Club)
}
