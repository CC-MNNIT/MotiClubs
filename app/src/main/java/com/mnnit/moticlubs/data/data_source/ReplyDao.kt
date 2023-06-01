package com.mnnit.moticlubs.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Reply

@Dao
interface ReplyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReply(reply: Reply)

    @Query("SELECT * FROM reply WHERE pid = :postID")
    suspend fun getRepliesByPost(postID: Long): List<Reply>

    @Delete
    suspend fun deleteReply(reply: Reply)
}
