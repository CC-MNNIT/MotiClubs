package com.example.notificationapp.app

import android.content.Context
import android.util.Log
import com.example.notificationapp.Constants
import com.example.notificationapp.data.network.API
import com.example.notificationapp.data.network.UserResponse
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging

object UserInstance {

    private const val TAG = "UserInstance"

    private var mInstance: UserResponse? = null

    fun getName() = mInstance?.name ?: ""
    fun getEmail() = mInstance?.email ?: ""
    fun getPhoneNumber() = mInstance?.phoneNumber ?: ""
    fun getRegNo() = mInstance?.registrationNumber ?: ""
    fun getGradYear() = mInstance?.graduationYear ?: ""
    fun getCourse() = mInstance?.course ?: ""
    fun getAvatar() = mInstance?.avatar ?: ""
    fun setAvatar(url: String) {
        mInstance?.avatar = url
    }

    fun isAdmin(): Boolean = mInstance?.admin?.isNotEmpty() ?: false

    fun isAdminOf(clubID: String): Boolean = mInstance?.admin?.contains(clubID) ?: false

    fun isSubscribedTo(clubID: String): Boolean = mInstance?.subscribed?.contains(clubID) ?: false

    fun refreshUserSession(user: FirebaseUser, ctx: Context, onDone: () -> Unit, onFail: () -> Unit) {
        updateAuthToken(user, ctx, {
            fetchUserInstance(ctx, {
                updateFCMToken(ctx)
                onDone()
            }, onFail)
        }, onFail)
    }

    private fun updateFCMToken(ctx: Context) {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                API.setFCMToken(getAuthToken(ctx), token, {
                    Log.d(TAG, "onResponse: token success")
                }) {}
            }
        }
    }

    fun fetchUserInstance(ctx: Context, onDone: () -> Unit, onFail: () -> Unit) {
        API.getUserData(getAuthToken(ctx), {
            mInstance = it
            onDone()
        }) { onFail() }
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