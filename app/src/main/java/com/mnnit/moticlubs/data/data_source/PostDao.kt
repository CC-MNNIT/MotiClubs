package com.mnnit.moticlubs.data.data_source

import androidx.room.*
import com.mnnit.moticlubs.domain.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(post: Post)

    @Query("SELECT * FROM post WHERE post.chid = :channelID AND post.page = :page ORDER BY post.time DESC")
    suspend fun getPostsFromChannel(channelID: Long, page: Int): List<Post>

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM post WHERE post.pid = :postID")
    suspend fun deletePostID(postID: Long)
}
