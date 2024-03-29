package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)

    @Query("SELECT * FROM user WHERE user.uid = :userId")
    suspend fun getUser(userId: Long): User?

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Delete
    suspend fun deleteUser(user: User)
}
