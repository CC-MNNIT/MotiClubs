package com.example.notificationapp.data.network.api

import com.example.notificationapp.data.network.ClubModel
import com.example.notificationapp.data.network.ProfilePicResponse
import com.example.notificationapp.data.network.UserModel
import com.example.notificationapp.data.network.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @POST("/user")
    fun saveUser(@Header("Authorization") auth: String?, @Body userModel: UserModel?): Call<UserResponse?>

    @GET("/clubs")
    fun getClubs(@Header("Authorization") auth: String?): Call<List<ClubModel>?>

    @GET("/user")
    fun getUserData(@Header("Authorization") auth: String?): Call<UserResponse?>

    @PUT("/user")
    fun updateProfilePic(@Header("Authorization") auth: String?, @Body avatar: String): Call<ProfilePicResponse?>
}
