package com.example.notificationapp.data.network

import com.example.notificationapp.data.network.api.RetrofitAccessObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object API {

    fun saveUser(
        auth: String?, userModel: UserModel?,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().saveUser(auth, userModel)
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse()
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) = onFailure(-1)
            })
    }

    fun getUserData(
        auth: String?,
        onResponse: (user: UserResponse) -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().getUserData(auth)
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) = onFailure(-1)
            })
    }

    fun updateProfilePic(
        auth: String?, url: String,
        onResponse: (profilePic: ProfilePicResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().updateProfilePic(auth, ProfilePicResponse(url))
            .enqueue(object : Callback<ProfilePicResponse?> {
                override fun onResponse(call: Call<ProfilePicResponse?>, response: Response<ProfilePicResponse?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<ProfilePicResponse?>, t: Throwable) = onFailure(-1)
            })
    }

    fun setFCMToken(
        auth: String?, token: String,
        onResponse: (fcmToken: FCMToken) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().setFCMToken(auth, FCMToken(token))
            .enqueue(object : Callback<FCMToken?> {
                override fun onResponse(call: Call<FCMToken?>, response: Response<FCMToken?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<FCMToken?>, t: Throwable) = onFailure(-1)
            })
    }

    fun getUserDetails(
        auth: String?, email: String,
        onResponse: (userDetail: UserDetailResponse) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().getUserDetails(auth, email)
            .enqueue(object : Callback<UserDetailResponse?> {
                override fun onResponse(call: Call<UserDetailResponse?>, response: Response<UserDetailResponse?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<UserDetailResponse?>, t: Throwable) = onFailure(-1)
            })
    }

    fun subscribeToClub(
        auth: String?, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().subscribeToClub(auth, ClubSubscriptionModel(clubID))
            .enqueue(object : Callback<ClubSubscriptionModel?> {
                override fun onResponse(call: Call<ClubSubscriptionModel?>, response: Response<ClubSubscriptionModel?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse()
                }

                override fun onFailure(call: Call<ClubSubscriptionModel?>, t: Throwable) = onFailure(-1)
            })
    }

    fun unsubscribeToClub(
        auth: String?, clubID: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().unsubscribeToClub(auth, ClubSubscriptionModel(clubID))
            .enqueue(object : Callback<ClubSubscriptionModel?> {
                override fun onResponse(call: Call<ClubSubscriptionModel?>, response: Response<ClubSubscriptionModel?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse()
                }

                override fun onFailure(call: Call<ClubSubscriptionModel?>, t: Throwable) = onFailure(-1)
            })
    }

    fun getClubs(
        auth: String?,
        onResponse: (clubList: List<ClubModel>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().getClubs(auth)
            .enqueue(object : Callback<List<ClubModel>?> {
                override fun onResponse(call: Call<List<ClubModel>?>, response: Response<List<ClubModel>?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<List<ClubModel>?>, t: Throwable) = onFailure(-1)
            })
    }

    fun getClubPosts(
        auth: String?, clubID: String,
        onResponse: (posts: List<PostResponse>) -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().getClubPosts(auth, clubID)
            .enqueue(object : Callback<List<PostResponse>?> {
                override fun onResponse(call: Call<List<PostResponse>?>, response: Response<List<PostResponse>?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse(body)
                }

                override fun onFailure(call: Call<List<PostResponse>?>, t: Throwable) = onFailure(-1)
            })
    }

    fun sendPost(
        auth: String?, clubID: String, message: String,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        RetrofitAccessObject.getRetrofitAccessObject().sendPost(auth, clubID, PostModel(message))
            .enqueue(object : Callback<PostResponse?> {
                override fun onResponse(call: Call<PostResponse?>, response: Response<PostResponse?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFailure(response.code())
                        return
                    }
                    onResponse()
                }

                override fun onFailure(call: Call<PostResponse?>, t: Throwable) = onFailure(-1)
            })
    }
}
