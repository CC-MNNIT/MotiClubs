package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(post: Post)

    @Query("SELECT * FROM post WHERE post.chid = :channelID ORDER BY post.time DESC")
    suspend fun getPostsFromChannel(channelID: Long): List<Post>

    @Delete
    suspend fun deletePost(post: Post)
}
