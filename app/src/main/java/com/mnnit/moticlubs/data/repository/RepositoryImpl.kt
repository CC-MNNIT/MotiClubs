package com.mnnit.moticlubs.data.repository

import android.app.Application
import androidx.room.withTransaction
import com.mnnit.moticlubs.data.datasource.LocalDatabase
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.ChannelMember
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.Stamp
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.repository.Repository

class RepositoryImpl(
    private val db: LocalDatabase,
    private val apiService: ApiService,
    private val application: Application,
) : Repository {

    private val dao = db.dao

    override fun getAPIService() = apiService
    override fun getApplication() = application

    // ----------- USER

    override suspend fun insertOrUpdateUser(user: User) {
        db.withTransaction { dao.insertOrUpdateUser(user) }
    }

    override suspend fun getUser(userID: Long): User? {
        return dao.getUser(userID)
    }

    override suspend fun getAllUsers(): List<User> {
        return dao.getAllUsers()
    }

    override suspend fun deleteUser(user: User) {
        db.withTransaction { dao.deleteUser(user) }
    }

    // ----------- CLUB

    override suspend fun insertOrUpdateClub(club: Club) {
        db.withTransaction { dao.insertOrUpdateClub(club) }
    }

    override suspend fun getClubs(): List<Club> {
        return dao.getClubs()
    }

    override suspend fun getClub(clubID: Long): Club {
        return dao.getClub(clubID)
    }

    override suspend fun deleteClub(club: Club) {
        db.withTransaction { dao.deleteClub(club) }
    }

    // ----------- ADMIN

    override suspend fun insertOrUpdateAdmin(admin: Admin) {
        db.withTransaction { dao.insertOrUpdateAdmin(admin) }
    }

    override suspend fun getAdmins(): List<AdminUser> {
        return dao.getAdmins()
    }

    override suspend fun deleteAdmin(userId: Long) {
        db.withTransaction { dao.deleteAdmin(userId) }
    }

    // ----------- CHANNEL

    override suspend fun insertOrUpdateChannel(channel: Channel) {
        db.withTransaction { dao.insertOrUpdateChannel(channel) }
    }

    override suspend fun getChannel(channelId: Long): Channel {
        return dao.getChannel(channelId)
    }

    override suspend fun getAllChannels(userId: Long): List<Channel> {
        return dao.getAllChannels()
    }

    override suspend fun deleteChannel(channel: Channel) {
        db.withTransaction { dao.deleteChannel(channel) }
    }

    // ----------- POST

    override suspend fun insertOrUpdatePost(post: Post) {
        db.withTransaction { dao.insertOrUpdatePost(post) }
    }

    override suspend fun getPostsFromChannel(channelID: Long, page: Int): List<Post> {
        return dao.getPostsFromChannel(channelID, page)
    }

    override suspend fun getPost(postId: Long): Post {
        return dao.getPost(postId)
    }

    override suspend fun deletePost(post: Post) {
        db.withTransaction { dao.deletePost(post) }
    }

    override suspend fun deletePostID(postID: Long) {
        db.withTransaction { dao.deletePostId(postID) }
    }

    // ----------- MEMBER

    override suspend fun insertOrUpdateMember(member: Member) {
        db.withTransaction { dao.insertOrUpdateMember(member) }
    }

    override suspend fun getMembers(channelId: Long): List<Member> {
        return dao.getMembers(channelId)
    }

    override suspend fun getChannelsForMember(userId: Long): List<ChannelMember> {
        return dao.getChannelsForMember(userId)
    }

    override suspend fun deleteMember(member: Member) {
        db.withTransaction { dao.deleteMember(member) }
    }

    // ----------- URL

    override suspend fun insertOrUpdateUrl(url: Url) {
        db.withTransaction { dao.insertOrUpdateUrl(url) }
    }

    override suspend fun getUrlsFromClub(clubID: Long): List<Url> {
        return dao.getUrlsFromClub(clubID)
    }

    override suspend fun deleteUrl(url: Url) {
        db.withTransaction { dao.deleteUrl(url) }
    }

    // ----------- VIEW

    override suspend fun insertOrUpdateView(view: View) {
        db.withTransaction { dao.insertOrUpdateView(view) }
    }

    override suspend fun getViewsFromPost(postID: Long): List<View> {
        return dao.getViewsFromPost(postID)
    }

    // ----------- REPLY

    override suspend fun insertOrUpdateReply(reply: Reply) {
        db.withTransaction { dao.insertOrUpdateReply(reply) }
    }

    override suspend fun getRepliesByPost(postID: Long, page: Int): List<Reply> {
        return dao.getRepliesByPost(postID, page)
    }

    override suspend fun deleteReply(reply: Reply) {
        db.withTransaction { dao.deleteReply(reply) }
    }

    override suspend fun deleteReplyID(replyID: Long) {
        db.withTransaction { dao.deleteReplyID(replyID) }
    }

    // ----------- STAMP

    override suspend fun insertOrUpdateStamp(stamp: Stamp) {
        db.withTransaction { dao.insertOrUpdateStamp(stamp) }
    }

    override suspend fun getStampByKey(key: String): Stamp? {
        return dao.getStampByKey(key)
    }

    override suspend fun deleteAllStamp() {
        db.withTransaction { dao.deleteAllStamp() }
    }
}
