package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.View

@Dao
interface ViewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateView(view: View)

    @Query("SELECT * FROM `view` WHERE pid = :postID")
    suspend fun getViewsFromPost(postID: Long): List<View>
}
