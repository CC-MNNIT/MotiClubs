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

    @Query("SELECT * FROM reply WHERE pid = :postId AND page = :page ORDER BY time DESC")
    suspend fun getRepliesByPost(postId: Long, page: Int): List<Reply>

    @Delete
    suspend fun deleteReply(reply: Reply)

    @Query("DELETE FROM reply WHERE time = :replyId")
    suspend fun deleteReplyID(replyId: Long)
}
