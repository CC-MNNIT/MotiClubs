package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.setAuthToken
import com.mnnit.moticlubs.domain.util.setUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    var showErrorScreen by mutableStateOf(false)
    var fetchingState by mutableStateOf(false)
    var showSplashScreen by mutableStateOf(true)

    fun getUser(
        firebaseUser: FirebaseUser?,
        onResponse: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        if (firebaseUser == null) {
            fetchingState = false
            showSplashScreen = false
            onFailure()
            return
        }

        fetchingState = true
        firebaseUser.getIdToken(true).addOnSuccessListener {
            val currentUserID = it.claims["userId"]?.toString()?.toLong() ?: -1
            application.setUserId(currentUserID)
            application.setAuthToken(it.token ?: "")

            fetchingState = false
            showErrorScreen = false
            onResponse()
        }.addOnCompleteListener {
            fetchingState = false
            showSplashScreen = false
            if (!it.isSuccessful) {
                onFailure()
//                showErrorScreen = true
            }
        }
    }

    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        application.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit().clear().apply()
    }

    init {
        FirebaseAuth.getInstance().addIdTokenListener { it: InternalTokenResult ->
            Log.d(TAG, "addIdTokenListener: called")
            application.setAuthToken(it.token ?: "")
            Log.d(TAG, "addIdTokenListener: saved")
        }
    }
}
