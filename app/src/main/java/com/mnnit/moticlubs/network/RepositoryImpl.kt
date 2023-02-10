package com.mnnit.moticlubs.network

import android.content.Context
import com.mnnit.moticlubs.getAuthToken
import com.mnnit.moticlubs.network.model.*
import okhttp3.ResponseBody
import retrofit2.Response

class RepositoryImpl(private val apiService: ApiService) : Repository {

    // ------------------------ USER ------------------------- //

    override suspend fun saveUser(ctx: Context, saveUserModel: SaveUserModel): ResponseModel<ResponseBody> {
        return controller(apiService.saveUser(ctx.getAuthToken(), saveUserModel))
    }

    override suspend fun getUserData(ctx: Context): ResponseModel<UserResponse> {
        return controller(apiService.getUserData(ctx.getAuthToken()))
    }

    override suspend fun getAllAdmins(ctx: Context): ResponseModel<List<AdminDetailResponse>> {
        return controller(apiService.getAllAdmins(ctx.getAuthToken()))
    }

    override suspend fun getUserDetails(ctx: Context, userID: Int): ResponseModel<AdminDetailResponse> {
        return controller(apiService.getUserDetails(ctx.getAuthToken(), userID = userID))
    }

    override suspend fun setProfilePicUrl(ctx: Context, url: String): ResponseModel<ResponseBody> {
        return controller(apiService.setProfilePicUrl(ctx.getAuthToken(), UpdateUserAvatarModel(url)))
    }

    override suspend fun setFCMToken(ctx: Context, fcmToken: String): ResponseModel<ResponseBody> {
        return controller(apiService.setFCMToken(ctx.getAuthToken(), FCMTokenModel(token = fcmToken)))
    }

    override suspend fun subscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody> {
        return controller(apiService.subscribeToClub(ctx.getAuthToken(), UserClubModel(clubID = clubID)))
    }

    override suspend fun unsubscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody> {
        return controller(apiService.unsubscribeToClub(ctx.getAuthToken(), UserClubModel(clubID = clubID)))
    }

    // ------------------------ CLUBS ------------------------- //

    override suspend fun getClubs(ctx: Context): ResponseModel<List<ClubModel>> {
        return controller(apiService.getClubs(ctx.getAuthToken()))
    }

    override suspend fun updateClub(
        ctx: Context,
        clubID: Int,
        updateClubModel: UpdateClubModel
    ): ResponseModel<ResponseBody> {
        return controller(apiService.updateClub(ctx.getAuthToken(), clubID = clubID, updateClubModel))
    }

    override suspend fun getSubscribersCount(ctx: Context, clubID: Int): ResponseModel<SubscriberCountResponse> {
        return controller(apiService.getSubscribersCount(ctx.getAuthToken(), clubID))
    }

    // ------------------------ POSTS ------------------------- //

    override suspend fun getPostsFromClubChannel(
        ctx: Context,
        clubID: Int,
        channelID: Int
    ): ResponseModel<List<PostModel>> {
        return controller(apiService.getPostsFromClubChannel(ctx.getAuthToken(), clubID, channelID))
    }

    override suspend fun sendPost(ctx: Context, pushPostModel: PushPostModel): ResponseModel<ResponseBody> {
        return controller(apiService.sendPost(ctx.getAuthToken(), pushPostModel))
    }

    override suspend fun updatePost(ctx: Context, postID: Int, message: String): ResponseModel<ResponseBody> {
        return controller(apiService.updatePost(ctx.getAuthToken(), postID, UpdatePostModel(message)))
    }

    override suspend fun deletePost(ctx: Context, postID: Int): ResponseModel<ResponseBody> {
        return controller(apiService.deletePost(ctx.getAuthToken(), postID))
    }

    // ------------------------- CHANNEL ---------------------- //

    override suspend fun getAllChannels(ctx: Context): ResponseModel<List<ChannelModel>> {
        return controller(apiService.getAllChannels(ctx.getAuthToken()))
    }

    override suspend fun getClubChannels(ctx: Context, clubID: Int): ResponseModel<List<ChannelModel>> {
        return controller(apiService.getClubChannels(ctx.getAuthToken(), clubID))
    }

    override suspend fun createChannel(ctx: Context, addChannelModel: AddChannelModel): ResponseModel<ResponseBody> {
        return controller(apiService.createChannel(ctx.getAuthToken(), addChannelModel))
    }

    override suspend fun updateChannelName(ctx: Context, channelID: Int, name: String): ResponseModel<ResponseBody> {
        return controller(apiService.updateChannelName(ctx.getAuthToken(), channelID, UpdateChannelModel(name)))
    }

    override suspend fun deleteChannel(ctx: Context, channelID: Int): ResponseModel<ResponseBody> {
        return controller(apiService.deleteChannel(ctx.getAuthToken(), channelID))
    }

    // ---------------------- URLs --------------------------- //

    override suspend fun getUrls(ctx: Context, clubID: Int): ResponseModel<List<UrlResponseModel>> {
        return controller(apiService.getUrls(ctx.getAuthToken(), clubID))
    }

    override suspend fun saveUrl(ctx: Context, clubID: Int, urlModel: UrlModel): ResponseModel<ResponseBody> {
        return controller(apiService.saveUrl(ctx.getAuthToken(), clubID, urlModel))
    }

    override suspend fun updateUrl(
        ctx: Context,
        urlID: Int,
        clubID: Int,
        urlModel: UrlModel
    ): ResponseModel<ResponseBody> {
        return controller(apiService.updateUrl(ctx.getAuthToken(), urlID, clubID, urlModel))
    }

    override suspend fun deleteUrl(ctx: Context, urlID: Int, clubID: Int): ResponseModel<ResponseBody> {
        return controller(apiService.deleteUrl(ctx.getAuthToken(), urlID, clubID, ClubUrlModel(clubID)))
    }

    // ---------------------------------------------------- //

    private inline fun <T, reified R> controller(response: Response<T>): ResponseModel<R> {
        try {
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                return Failed(response.code(), response.message())
            }
            return Success(body as R)
        } catch (e: Exception) {
            return Failed(-1, e.message ?: "")
        }
    }
}
