package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Constants
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setAuthToken
import com.mnnit.moticlubs.domain.util.setUserId
import com.mnnit.moticlubs.domain.util.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    var showErrorScreen by publishedStateOf(false)
    var fetchingState by publishedStateOf(false)
    var showSplashScreen by publishedStateOf(true)

    fun getUser(
        firebaseUser: FirebaseUser?,
        onResponse: () -> Unit = {},
        onFailure: () -> Unit = {},
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
        viewModelScope.launch {
            repository.deleteAllStamp()
        }
    }

    init {
        FirebaseAuth.getInstance().addIdTokenListener { it: InternalTokenResult ->
            Log.d(TAG, "addIdTokenListener: called")
            application.setAuthToken(it.token ?: "")
            Log.d(TAG, "addIdTokenListener: saved")
        }
    }
}
