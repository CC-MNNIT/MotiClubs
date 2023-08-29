package com.mnnit.moticlubs.data.data_source

import androidx.room.*
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
