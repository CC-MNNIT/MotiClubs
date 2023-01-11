package com.example.notificationapp.api

import com.example.notificationapp.app.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object API {

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

    private interface Api {
        @POST("/user")
        fun saveUser(@Header("Authorization") auth: String?, @Body userModel: UserModel?): Call<UserResponse?>

        @GET("/user")
        fun getUserData(@Header("Authorization") auth: String?): Call<UserResponse?>

        @POST("/user/avatar")
        fun updateProfilePic(@Header("Authorization") auth: String?, @Body avatar: ProfilePicResponse): Call<ProfilePicResponse?>

        @POST("/user/fcmtoken")
        fun setFCMToken(@Header("Authorization") auth: String?, @Body token: FCMToken): Call<FCMToken?>

        @GET("/user/{email}")
        fun getUserDetails(
            @Header("Authorization") auth: String?,
            @Path("email") email: String,
        ): Call<UserDetailResponse?>

        @PUT("/user/subscribe")
        fun subscribeToClub(
            @Header("Authorization") auth: String?,
            @Body club: ClubSubscriptionModel
        ): Call<ClubSubscriptionModel?>

        @PUT("/user/unsubscribe")
        fun unsubscribeToClub(
            @Header("Authorization") auth: String?,
            @Body club: ClubSubscriptionModel
        ): Call<ClubSubscriptionModel?>

        @GET("/clubs")
        fun getClubs(@Header("Authorization") auth: String?): Call<List<ClubModel>?>

        @GET("/posts/{club}")
        fun getClubPosts(
            @Header("Authorization") auth: String?,
            @Path("club") clubID: String,
        ): Call<List<PostResponse>?>

        @POST("/posts/{club}")
        fun sendPost(
            @Header("Authorization") auth: String?,
            @Path("club") clubID: String?,
            @Body postModel: PostModel
        ): Call<PostResponse?>
    }

    fun saveUser(
        auth: String?, userModel: UserModel?,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        getRetrofitAccessObject().saveUser(auth, userModel)
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
        getRetrofitAccessObject().getUserData(auth)
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
        getRetrofitAccessObject().updateProfilePic(auth, ProfilePicResponse(url))
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
        getRetrofitAccessObject().setFCMToken(auth, FCMToken(token))
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
        getRetrofitAccessObject().getUserDetails(auth, email)
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
        getRetrofitAccessObject().subscribeToClub(auth, ClubSubscriptionModel(clubID))
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
        getRetrofitAccessObject().unsubscribeToClub(auth, ClubSubscriptionModel(clubID))
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
        getRetrofitAccessObject().getClubs(auth)
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
        getRetrofitAccessObject().getClubPosts(auth, clubID)
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
        getRetrofitAccessObject().sendPost(auth, clubID, PostModel(message))
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
