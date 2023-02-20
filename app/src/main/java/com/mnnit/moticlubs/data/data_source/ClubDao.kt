package com.mnnit.moticlubs.data.data_source

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

    @Query("SELECT * FROM club WHERE club.cid = :clubID")
    suspend fun getClub(clubID: Int): Club

    @Delete
    suspend fun deleteClub(club: Club)
}
