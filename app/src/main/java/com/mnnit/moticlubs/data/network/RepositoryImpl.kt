package com.mnnit.moticlubs.data.network

import android.content.Context
import com.mnnit.moticlubs.data.network.model.*
import com.mnnit.moticlubs.getAuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class RepositoryImpl(private val apiService: ApiService) : Repository {

    // ------------------------ USER ------------------------- //

    override suspend fun saveUser(ctx: Context, saveUserDto: SaveUserDto): ResponseModel<ResponseBody> {
        return controller { apiService.saveUser(ctx.getAuthToken(), saveUserDto) }
    }

    override suspend fun getUserData(ctx: Context): ResponseModel<UserResponse> {
        return controller { apiService.getUserData(ctx.getAuthToken()) }
    }

    override suspend fun getAllAdmins(ctx: Context): ResponseModel<List<AdminDetailResponse>> {
        return controller { apiService.getAllAdmins(ctx.getAuthToken()) }
    }

    override suspend fun getUserDetails(ctx: Context, userID: Int): ResponseModel<AdminDetailResponse> {
        return controller { apiService.getUserDetails(ctx.getAuthToken(), userID = userID) }
    }

    override suspend fun setProfilePicUrl(ctx: Context, url: String): ResponseModel<ResponseBody> {
        return controller { apiService.setProfilePicUrl(ctx.getAuthToken(), UpdateUserAvatarDto(url)) }
    }

    override suspend fun setFCMToken(ctx: Context, fcmToken: String): ResponseModel<ResponseBody> {
        return controller { apiService.setFCMToken(ctx.getAuthToken(), FCMTokenDto(token = fcmToken)) }
    }

    override suspend fun subscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody> {
        return controller { apiService.subscribeToClub(ctx.getAuthToken(), UserClubDto(clubID = clubID)) }
    }

    override suspend fun unsubscribeClub(ctx: Context, clubID: Int): ResponseModel<ResponseBody> {
        return controller { apiService.unsubscribeToClub(ctx.getAuthToken(), UserClubDto(clubID = clubID)) }
    }

    // ------------------------ CLUBS ------------------------- //

    override suspend fun getClubs(ctx: Context): ResponseModel<List<ClubModel>> {
        return controller { apiService.getClubs(ctx.getAuthToken()) }
    }

    override suspend fun updateClub(
        ctx: Context,
        clubID: Int,
        updateClubDto: UpdateClubDto
    ): ResponseModel<ResponseBody> {
        return controller { apiService.updateClub(ctx.getAuthToken(), clubID = clubID, updateClubDto) }
    }

    override suspend fun getSubscribersCount(ctx: Context, clubID: Int): ResponseModel<SubscriberCountDto> {
        return controller { apiService.getSubscribersCount(ctx.getAuthToken(), clubID) }
    }

    // ------------------------ POSTS ------------------------- //

    override suspend fun getPostsFromClubChannel(
        ctx: Context,
        clubID: Int,
        channelID: Int
    ): ResponseModel<List<PostDto>> {
        return controller { apiService.getPostsFromClubChannel(ctx.getAuthToken(), clubID, channelID) }
    }

    override suspend fun sendPost(ctx: Context, pushPostModel: PushPostModel): ResponseModel<ResponseBody> {
        return controller { apiService.sendPost(ctx.getAuthToken(), pushPostModel) }
    }

    override suspend fun updatePost(ctx: Context, postID: Int, message: String): ResponseModel<ResponseBody> {
        return controller { apiService.updatePost(ctx.getAuthToken(), postID, UpdatePostModel(message)) }
    }

    override suspend fun deletePost(ctx: Context, postID: Int, channelID: Int): ResponseModel<ResponseBody> {
        return controller { apiService.deletePost(ctx.getAuthToken(), postID, channelID) }
    }

    // ------------------------- CHANNEL ---------------------- //

    override suspend fun getAllChannels(ctx: Context): ResponseModel<List<ChannelDto>> {
        return controller { apiService.getAllChannels(ctx.getAuthToken()) }
    }

    override suspend fun getClubChannels(ctx: Context, clubID: Int): ResponseModel<List<ChannelDto>> {
        return controller { apiService.getClubChannels(ctx.getAuthToken(), clubID) }
    }

    override suspend fun createChannel(ctx: Context, addChannelDto: AddChannelDto): ResponseModel<ResponseBody> {
        return controller { apiService.createChannel(ctx.getAuthToken(), addChannelDto) }
    }

    override suspend fun updateChannelName(ctx: Context, channelID: Int, name: String): ResponseModel<ResponseBody> {
        return controller { apiService.updateChannelName(ctx.getAuthToken(), channelID, UpdateChannelDto(name)) }
    }

    override suspend fun deleteChannel(ctx: Context, channelID: Int): ResponseModel<ResponseBody> {
        return controller { apiService.deleteChannel(ctx.getAuthToken(), channelID) }
    }

    // ---------------------- URLs --------------------------- //

    override suspend fun getUrls(ctx: Context, clubID: Int): ResponseModel<List<UrlResponseModel>> {
        return controller { apiService.getUrls(ctx.getAuthToken(), clubID) }
    }

    override suspend fun pushUrls(ctx: Context, clubID: Int, list: List<UrlModel>): ResponseModel<ResponseBody> {
        return controller { apiService.pushUrls(ctx.getAuthToken(), clubID, UrlDto(list)) }
    }

    // ---------------------- VIEWs --------------------------- //

    override suspend fun getViews(ctx: Context, postID: Int): ResponseModel<ViewCountDto> {
        return controller { apiService.getViews(ctx.getAuthToken(), postID) }
    }

    override suspend fun addViews(ctx: Context, postID: Int): ResponseModel<ResponseBody> {
        return controller { apiService.addView(ctx.getAuthToken(), ViewPostDto(postID)) }
    }

    // ---------------------------------------------------- //

    private suspend inline fun <T, reified R> controller(crossinline invoke: (suspend () -> Response<T>)): ResponseModel<R> {
        try {
            val response = withContext(Dispatchers.IO) { invoke() }
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