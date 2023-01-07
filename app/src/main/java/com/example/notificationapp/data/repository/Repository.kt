package com.example.notificationapp.data.repository

import android.util.Log
import com.example.notificationapp.data.network.UserModel
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {

    fun saveUser(auth: String?, userModel: UserModel): UserResponse? {
//        val userResponse = UserResponse()
        RetrofitAccessObject.getRetrofitAccessObject().saveUser(auth, userModel).enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                Log.i(
                    "RequestBody", """${call.request().url()} $response$userModel"""
                )
                if (response.code() == 200) {
                    try {
                        if (response.body() == null) throw Exception("Unqualified response")
                        if (response.isSuccessful && response.body() != null) {
//                            userResponse = response.body();
                        }
                    } catch (exception: Exception) {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {}
        })
        return null
    }
}