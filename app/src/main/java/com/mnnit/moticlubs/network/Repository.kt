package com.mnnit.moticlubs.network

import android.content.Context
import com.mnnit.moticlubs.network.model.*
import okhttp3.ResponseBody

open class ResponseModel<T>(val t: T?, val errCode: Int, val errMsg: String)

data class Failed<T>(val code: Int, val msg: String) : ResponseModel<T>(null, code, msg)

data class Success<T>(val obj: T) : ResponseModel<T>(obj, 200, "Success")

interface Repository : UserRepository, ClubRepository, PostsRepository, ChannelRepository, UrlRepository

interface UserRepository {

    suspend fun saveUser(ctx: Context, saveUserModel: SaveUserModel): ResponseModel<ResponseBody>

    suspend fun getUserData(ctx: Context): ResponseModel<UserResponse>

    suspend fun getAllAdmins(ctx: Context): ResponseModel<List<AdminDetailResponse>>

    suspend fun getUserDetails(ctx: Context, userID: Int): ResponseModel<AdminDetailResponse>

    suspend fun setProfilePicUrl(ctx: Context, url: String): ResponseModel<ResponseBody>

    suspend fun setFCMToken(ctx: Context, fcmToken: String): ResponseModel<ResponseBody>

    suspend fun subscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody>

    suspend fun unsubscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody>
}

interface ClubRepository {

    suspend fun getClubs(ctx: Context): ResponseModel<List<ClubModel>>

    suspend fun updateClub(ctx: Context, clubID: Int, updateClubModel: UpdateClubModel): ResponseModel<ResponseBody>

    suspend fun getSubscribersCount(ctx: Context, clubID: Int): ResponseModel<SubscriberCountResponse>
}

interface PostsRepository {

    suspend fun getPostsFromClubChannel(ctx: Context, clubID: Int, channelID: Int): ResponseModel<List<PostModel>>

    suspend fun sendPost(ctx: Context, pushPostModel: PushPostModel): ResponseModel<ResponseBody>

    suspend fun updatePost(ctx: Context, postID: Int, message: String): ResponseModel<ResponseBody>

    suspend fun deletePost(ctx: Context, postID: Int): ResponseModel<ResponseBody>
}

interface ChannelRepository {

    suspend fun getAllChannels(ctx: Context): ResponseModel<List<ChannelModel>>

    suspend fun getClubChannels(ctx: Context, clubID: Int): ResponseModel<List<ChannelModel>>

    suspend fun createChannel(ctx: Context, addChannelModel: AddChannelModel): ResponseModel<ResponseBody>

    suspend fun updateChannelName(ctx: Context, channelID: Int, name: String): ResponseModel<ResponseBody>

    suspend fun deleteChannel(ctx: Context, channelID: Int): ResponseModel<ResponseBody>
}

interface UrlRepository {

    suspend fun getUrls(ctx: Context, clubID: Int): ResponseModel<List<UrlResponseModel>>

    suspend fun pushUrls(ctx: Context, clubID: Int, list: List<UrlModel>): ResponseModel<ResponseBody>
}
