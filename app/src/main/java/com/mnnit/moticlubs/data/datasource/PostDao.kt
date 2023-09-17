package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePost(post: Post)

    @Query("SELECT * FROM post WHERE post.pid = :postId")
    suspend fun getPost(postId: Long): Post

    @Query("SELECT * FROM post WHERE post.chid = :channelId AND post.page = :page ORDER BY post.updated DESC")
    suspend fun getPostsFromChannel(channelId: Long, page: Int): List<Post>

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM post WHERE post.pid = :postId")
    suspend fun deletePostId(postId: Long)
}
