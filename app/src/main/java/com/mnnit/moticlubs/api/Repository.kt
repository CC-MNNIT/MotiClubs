package com.mnnit.moticlubs.api

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.connectionAvailable
import com.mnnit.moticlubs.getAuthToken
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Repository {

    private var mApi: Api? = null

    private fun getRetrofitAccessObject(): Api {
        if (mApi == null) {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(
                    OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES) // write timeout
                        .readTimeout(1, TimeUnit.MINUTES) // read timeout
                        .build()
                )
                .build()
            mApi = retrofit.create(Api::class.java)
        }
        return mApi!!
    }

    fun ViewModel.saveUser(
        ctx: Context, userModel: UserModel?,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().saveUser(ctx.getAuthToken(), userModel)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.getUserData(
        ctx: Context,
        onResponse: (user: UserResponse) -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().getUserData(ctx.getAuthToken())
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.updateProfilePic(
        ctx: Context, url: String,
        onResponse: (profilePic: ProfilePicResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject()
                .updateProfilePic(ctx.getAuthToken(), ProfilePicResponse(url))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.setFCMToken(
        ctx: Context, token: String,
        onResponse: (fcmToken: FCMToken) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().setFCMToken(ctx.getAuthToken(), FCMToken(token))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.getUserDetails(
        ctx: Context, email: String,
        onResponse: (userDetail: UserDetailResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getUserDetails(ctx.getAuthToken(), email)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.subscribeToClub(
        ctx: Context, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject()
                .subscribeToClub(ctx.getAuthToken(), ClubSubscriptionModel(clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.unsubscribeToClub(
        ctx: Context, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject()
                .unsubscribeToClub(ctx.getAuthToken(), ClubSubscriptionModel(clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.getClubs(
        ctx: Context,
        onResponse: (clubList: List<ClubModel>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().getClubs(ctx.getAuthToken())
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.getClubPosts(
        ctx: Context, clubID: String,
        onResponse: (posts: List<PostResponse>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().getClubPosts(ctx.getAuthToken(), clubID)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.sendPost(
        ctx: Context, clubID: String, message: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().sendPost(ctx.getAuthToken(), PostModel(message, clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.updatePost(
        ctx: Context, postID: String, message: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().updatePost(ctx.getAuthToken(), postID, UpdatePostModel(message))
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.deletePost(
        ctx: Context, postID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().deletePost(ctx.getAuthToken(), postID)
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.updateClub(
        ctx: Context, clubID: String, clubDTO: ClubDTO,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().updateClub(ctx.getAuthToken(), clubID, clubDTO)
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }


    fun ViewModel.getMembersCount(
        ctx: Context,
        clubID: String,
        onResponse: (subscriberCountResponse: SubscriberCountResponse) -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            if (!ctx.connectionAvailable()) {
                onFailure(-1)
                return@launch
            }
            val response = getRetrofitAccessObject().getMembersCount(ctx.getAuthToken(), clubID)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }
}