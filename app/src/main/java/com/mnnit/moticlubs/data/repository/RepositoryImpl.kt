package com.mnnit.moticlubs.data.repository

import android.app.Application
import com.mnnit.moticlubs.data.data_source.LocalDao
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.domain.model.*
import com.mnnit.moticlubs.domain.repository.Repository

class RepositoryImpl(
    private val dao: LocalDao,
    private val apiService: ApiService,
    private val application: Application
) : Repository {

    override fun getAPIService() = apiService
    override fun getApplication() = application

    // ----------- USER

    override suspend fun insertOrUpdateUser(user: User) {
        dao.insertOrUpdateUser(user)
    }

    override suspend fun getUser(userID: Int): User? {
        return dao.getUser(userID)
    }

    // ----------- CLUB

    override suspend fun insertOrUpdateClub(club: Club) {
        dao.insertOrUpdateClub(club)
    }

    override suspend fun getClubs(): List<Club> {
        return dao.getClubs()
    }

    override suspend fun getClub(clubID: Int): Club {
        return dao.getClub(clubID)
    }

    // ----------- ADMIN

    override suspend fun insertOrUpdateAdmin(admin: Admin) {
        dao.insertOrUpdateAdmin(admin)
    }

    override suspend fun getAdmins(): List<Admin> {
        return dao.getAdmins()
    }

    override suspend fun deleteAdmin(admin: Admin) {
        dao.deleteAdmin(admin)
    }

    // ----------- CHANNEL

    override suspend fun insertOrUpdateChannel(channel: Channel) {
        dao.insertOrUpdateChannel(channel)
    }

    override suspend fun getChannels(): List<Channel> {
        return dao.getChannels()
    }

    override suspend fun deleteChannel(channel: Channel) {
        dao.deleteChannel(channel)
    }

    // ----------- POST

    override suspend fun insertOrUpdatePost(post: Post) {
        dao.insertOrUpdatePost(post)
    }

    override suspend fun getPostsFromChannel(channelID: Long): List<Post> {
        return dao.getPostsFromChannel(channelID)
    }

    override suspend fun deletePost(post: Post) {
        dao.deletePost(post)
    }

    // ----------- SUBSCRIBER

    override suspend fun insertOrUpdateSubscriber(subscriber: Subscriber) {
        dao.insertOrUpdateSubscriber(subscriber)
    }

    override suspend fun getSubscribers(clubID: Int): List<Subscriber> {
        return dao.getSubscribers(clubID)
    }

    override suspend fun deleteSubscriber(subscriber: Subscriber) {
        dao.deleteSubscriber(subscriber)
    }

    // ----------- URL

    override suspend fun insertOrUpdateUrl(url: Url) {
        dao.insertOrUpdateUrl(url)
    }

    override suspend fun getUrlsFromClub(clubID: Int): List<Url> {
        return dao.getUrlsFromClub(clubID)
    }

    override suspend fun deleteUrl(url: Url) {
        dao.deleteUrl(url)
    }

    // ----------- VIEW

    override suspend fun insertOrUpdateView(view: View) {
        dao.insertOrUpdateView(view)
    }

    override suspend fun getViewsFromPost(postID: Long): List<View> {
        return dao.getViewsFromPost(postID)
    }
}
