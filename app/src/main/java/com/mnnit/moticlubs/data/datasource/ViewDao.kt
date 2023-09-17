package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.View

@Dao
interface ViewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateView(view: View)

    @Query("SELECT * FROM `view` WHERE pid = :postId")
    suspend fun getViewsFromPost(postId: Long): List<View>
}
