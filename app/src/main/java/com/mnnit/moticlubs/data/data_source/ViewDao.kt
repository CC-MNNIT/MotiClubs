package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.View

@Dao
interface ViewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateView(view: View)

    @Query("SELECT * FROM `view` WHERE pid = :postId")
    suspend fun getViewsFromPost(postId: Long): List<View>
}
