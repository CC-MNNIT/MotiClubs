package com.mnnit.moticlubs.data.repository

import android.app.Application
import androidx.room.withTransaction
import com.mnnit.moticlubs.data.data_source.LocalDatabase
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.repository.Repository

class RepositoryImpl(
    private val db: LocalDatabase,
    private val apiService: ApiService,
    private val application: Application
) : Repository {

    private val dao = db.dao

    override fun getAPIService() = apiService
    override fun getApplication() = application

    // ----------- USER

    override suspend fun insertOrUpdateUser(user: User) {
        db.withTransaction { dao.insertOrUpdateUser(user) }
    }

    override suspend fun getUser(userID: Int): User? {
        return dao.getUser(userID)
    }

    // ----------- CLUB

    override suspend fun insertOrUpdateClub(club: Club) {
        db.withTransaction { dao.insertOrUpdateClub(club) }
    }

    override suspend fun getClubs(): List<Club> {
        return dao.getClubs()
    }

    override suspend fun getClub(clubID: Int): Club {
        return dao.getClub(clubID)
    }

    override suspend fun deleteClub(club: Club) {
        db.withTransaction { dao.deleteClub(club) }
    }

    // ----------- ADMIN

    override suspend fun insertOrUpdateAdmin(admin: Admin) {
        db.withTransaction { dao.insertOrUpdateAdmin(admin) }
    }

    override suspend fun getAdmins(): List<Admin> {
        return dao.getAdmins()
    }

    override suspend fun deleteAdmin(admin: Admin) {
        db.withTransaction { dao.deleteAdmin(admin) }
    }

    // ----------- CHANNEL

    override suspend fun insertOrUpdateChannel(channel: Channel) {
        db.withTransaction { dao.insertOrUpdateChannel(channel) }
    }

    override suspend fun getChannels(): List<Channel> {
        return dao.getChannels()
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

    override suspend fun deletePost(post: Post) {
        db.withTransaction { dao.deletePost(post) }
    }

    // ----------- SUBSCRIBER

    override suspend fun insertOrUpdateSubscriber(subscriber: Subscriber) {
        db.withTransaction { dao.insertOrUpdateSubscriber(subscriber) }
    }

    override suspend fun getSubscribers(clubID: Int): List<Subscriber> {
        return dao.getSubscribers(clubID)
    }

    override suspend fun deleteSubscriber(subscriber: Subscriber) {
        db.withTransaction { dao.deleteSubscriber(subscriber) }
    }

    // ----------- URL

    override suspend fun insertOrUpdateUrl(url: Url) {
        db.withTransaction { dao.insertOrUpdateUrl(url) }
    }

    override suspend fun getUrlsFromClub(clubID: Int): List<Url> {
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
}
