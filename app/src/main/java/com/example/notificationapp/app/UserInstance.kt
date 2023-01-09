package com.example.notificationapp.app

import android.content.Context
import android.util.Log
import com.example.notificationapp.Constants
import com.example.notificationapp.data.network.FCMToken
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserInstance {

    private const val TAG = "UserInstance"

    private var mInstance: UserResponse? = null

    fun setUserInstance(user: UserResponse) {
        if (mInstance != null) return
        mInstance = user
    }

    fun isAdmin(): Boolean = mInstance?.admin?.isNotEmpty() ?: false

    fun isAdminOf(clubID: String): Boolean = mInstance?.admin?.contains(clubID) ?: false

    fun refreshUserSession(user: FirebaseUser, ctx: Context, onDone: () -> Unit, onFail: () -> Unit) {
        updateAuthToken(user, ctx, {
            initInstance(ctx, {
                updateFCMToken(ctx)
                onDone()
            }, onFail)
        }, onFail)
    }

    private fun updateFCMToken(ctx: Context) {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                RetrofitAccessObject.getRetrofitAccessObject().setFCMToken(getAuthToken(ctx), FCMToken(token))
                    .enqueue(object : Callback<FCMToken?> {
                        override fun onResponse(call: Call<FCMToken?>, response: Response<FCMToken?>) {
                            if (response.isSuccessful && response.body() != null) {
                                Log.d(TAG, "onResponse: token success")
                            }
                        }

                        override fun onFailure(call: Call<FCMToken?>, t: Throwable) {}
                    })
            }
        }
    }

    private fun initInstance(ctx: Context, onDone: () -> Unit, onFail: () -> Unit) {
        RetrofitAccessObject.getRetrofitAccessObject().getUserData(getAuthToken(ctx))
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    val user = response.body()
                    if (!response.isSuccessful || user == null) {
                        Log.d(TAG, "onResponse: init_instance: err: ${response.code()}")
                        onFail()
                        return
                    }
                    Log.d(TAG, "onResponse: init_instance: success")
                    mInstance = user
                    onDone()
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {}
            })
    }

    private fun updateAuthToken(user: FirebaseUser, ctx: Context, onDone: () -> Unit, onFail: () -> Unit) {
        user.getIdToken(true).addOnSuccessListener {
            val token = it.token
            if (token == null) {
                Log.d(TAG, "updateAuthToken: token was null")
                onFail()
                return@addOnSuccessListener
            }
            ctx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()
                .putString(Constants.TOKEN, token).apply()
            Log.d(TAG, "updateAuthToken: success")
            onDone()
        }
    }

    fun getAuthToken(ctx: Context): String {
        return ctx.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).getString(Constants.TOKEN, "") ?: ""
    }
}