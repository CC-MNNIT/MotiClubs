package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.Admin

@Dao
interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdmin(admin: Admin)

    @Query("SELECT * FROM admin")
    suspend fun getAdmins(): List<Admin>

    @Delete
    suspend fun deleteAdmin(admin: Admin)
}
