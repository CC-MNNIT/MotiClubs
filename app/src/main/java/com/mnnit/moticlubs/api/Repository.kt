package com.mnnit.moticlubs.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.mnnit.moticlubs.Constants
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
        auth: String?, userModel: UserModel?,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().saveUser(auth, userModel)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.getUserData(
        auth: String?,
        onResponse: (user: UserResponse) -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getUserData(auth)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.updateProfilePic(
        auth: String?, url: String,
        onResponse: (profilePic: ProfilePicResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().updateProfilePic(auth, ProfilePicResponse(url))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.setFCMToken(
        auth: String?, token: String,
        onResponse: (fcmToken: FCMToken) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().setFCMToken(auth, FCMToken(token))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.getUserDetails(
        auth: String?, email: String,
        onResponse: (userDetail: UserDetailResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getUserDetails(auth, email)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.subscribeToClub(
        auth: String?, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().subscribeToClub(auth, ClubSubscriptionModel(clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.unsubscribeToClub(
        auth: String?, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().unsubscribeToClub(auth, ClubSubscriptionModel(clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.getClubs(
        auth: String?,
        onResponse: (clubList: List<ClubModel>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getClubs(auth)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.getClubPosts(
        auth: String?, clubID: String,
        onResponse: (posts: List<PostResponse>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getClubPosts(auth, clubID)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }

    fun ViewModel.sendPost(
        auth: String?, clubID: String, message: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().sendPost(auth, PostModel(message, clubID))
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.updatePost(
        auth: String?, postID: String, message: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().updatePost(auth, postID, UpdatePostModel(message))
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.deletePost(
        auth: String?, postID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().deletePost(auth, postID)
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }

    fun ViewModel.updateClub(
        auth: String?, clubID: String, clubDTO: ClubDTO,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().updateClub(auth, clubID, clubDTO)
            if (!response.isSuccessful) {
                onFailure(response.code())
                return@launch
            }
            onResponse()
        }
    }


    fun ViewModel.getMembersCount(
        auth: String?,
        clubID: String,
        onResponse: (subscriberCountResponse: SubscriberCountResponse) -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = getRetrofitAccessObject().getMembersCount(auth, clubID)
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                onFailure(response.code())
                return@launch
            }
            onResponse(body)
        }
    }
}