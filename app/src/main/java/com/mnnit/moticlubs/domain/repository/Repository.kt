package com.mnnit.moticlubs.domain.repository

import android.app.Application
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.domain.model.*

interface Repository {

    fun getAPIService(): ApiService
    fun getApplication(): Application

    // ----------- USER

    suspend fun insertOrUpdateUser(user: User)

    suspend fun getUser(userID: Long): User?

    suspend fun getAllUsers(): List<User>

    suspend fun deleteUser(user: User)

    // ----------- CLUB

    suspend fun insertOrUpdateClub(club: Club)

    suspend fun getClubs(): List<Club>

    suspend fun getClub(clubID: Int): Club

    suspend fun deleteClub(club: Club)

    // ----------- ADMIN

    suspend fun insertOrUpdateAdmin(admin: Admin)

    suspend fun getAdmins(): List<Admin>

    suspend fun deleteAdmin(admin: Admin)

    // ----------- CHANNEL

    suspend fun insertOrUpdateChannel(channel: Channel)

    suspend fun getChannels(): List<Channel>

    suspend fun deleteChannel(channel: Channel)

    // ----------- POST

    suspend fun insertOrUpdatePost(post: Post)

    suspend fun getPostsFromChannel(channelID: Long, page: Int): List<Post>

    suspend fun deletePost(post: Post)

    suspend fun deletePostID(postID: Long)

    // ----------- SUBSCRIBER

    suspend fun insertOrUpdateSubscriber(subscriber: Subscriber)

    suspend fun getSubscribers(clubID: Int): List<Subscriber>

    suspend fun deleteSubscriber(subscriber: Subscriber)

    // ----------- URL

    suspend fun insertOrUpdateUrl(url: Url)

    suspend fun getUrlsFromClub(clubID: Int): List<Url>

    suspend fun deleteUrl(url: Url)

    // ----------- VIEW

    suspend fun insertOrUpdateView(view: View)

    suspend fun getViewsFromPost(postID: Long): List<View>

    // ----------- REPLY

    suspend fun insertOrUpdateReply(reply: Reply)

    suspend fun getRepliesByPost(postID: Long): List<Reply>

    suspend fun deleteReply(reply: Reply)

    suspend fun deleteReplyID(replyID: Long)
}
