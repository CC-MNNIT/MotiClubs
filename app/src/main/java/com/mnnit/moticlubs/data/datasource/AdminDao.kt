package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.AdminUser

@Dao
interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdmin(admin: Admin)

    @Query("SELECT * FROM user INNER JOIN admin ON user.uid = admin.uid")
    suspend fun getAdmins(): List<AdminUser>

    @Query("DELETE FROM admin WHERE admin.uid = :userId")
    suspend fun deleteAdmin(userId: Long)
}
