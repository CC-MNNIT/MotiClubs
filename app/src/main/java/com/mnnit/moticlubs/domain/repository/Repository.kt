package com.mnnit.moticlubs.domain.repository

import android.app.Application
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View

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

    suspend fun getClub(clubID: Long): Club

    suspend fun deleteClub(club: Club)

    // ----------- ADMIN

    suspend fun insertOrUpdateAdmin(admin: Admin)

    suspend fun getAdmins(): List<Admin>

    suspend fun deleteAdmin(admin: Admin)

    // ----------- CHANNEL

    suspend fun insertOrUpdateChannel(channel: Channel)

    suspend fun getChannel(channelId: Long): Channel

    suspend fun getAllChannels(): List<Channel>

    suspend fun deleteChannel(channel: Channel)

    // ----------- POST

    suspend fun insertOrUpdatePost(post: Post)

    suspend fun getPostsFromChannel(channelID: Long, page: Int): List<Post>

    suspend fun deletePost(post: Post)

    suspend fun deletePostID(postID: Long)

    // ----------- MEMBER

    suspend fun insertOrUpdateMember(member: Member)

    suspend fun getMembers(channelId: Long): List<Member>

    suspend fun deleteMember(member: Member)

    // ----------- URL

    suspend fun insertOrUpdateUrl(url: Url)

    suspend fun getUrlsFromClub(clubID: Long): List<Url>

    suspend fun deleteUrl(url: Url)

    // ----------- VIEW

    suspend fun insertOrUpdateView(view: View)

    suspend fun getViewsFromPost(postID: Long): List<View>

    // ----------- REPLY

    suspend fun insertOrUpdateReply(reply: Reply)

    suspend fun getRepliesByPost(postID: Long, page: Int): List<Reply>

    suspend fun deleteReply(reply: Reply)

    suspend fun deleteReplyID(replyID: Long)
}
