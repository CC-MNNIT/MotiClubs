package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.getUserID
import com.mnnit.moticlubs.setAuthToken
import com.mnnit.moticlubs.setUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    private val userUseCases: UserUseCases
) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    var showErrorScreen by mutableStateOf(false)
    var fetchingState by mutableStateOf(false)
    var showSplashScreen by mutableStateOf(true)
    var user by mutableStateOf(User())

    private var getUserJob: Job? = null
    private var updateUserJob: Job? = null

    fun getUser(firebaseUser: FirebaseUser?, onResponse: () -> Unit = {}, onFailure: () -> Unit = {}) {
        if (firebaseUser == null) {
            fetchingState = false
            showSplashScreen = false
            onFailure()
            return
        }

        fetchingState = true
        firebaseUser.getIdToken(true).addOnSuccessListener {
            application.setUserID(it.claims["userId"]?.toString()?.toInt() ?: -1)
            application.setAuthToken(it.token ?: "")

            getUserJob?.cancel()
            getUserJob = userUseCases.getUser(application.getUserID()).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        resource.data?.let { m -> user = m }
                        fetchingState = true
                        showErrorScreen = false
                    }
                    is Resource.Success -> {
                        fetchingState = false
                        showErrorScreen = false
                        showSplashScreen = false

                        user = resource.data
                        onResponse()
                    }
                    is Resource.Error -> {
                        fetchingState = false
                        showSplashScreen = false
                        onFailure()
                    }
                }
            }.launchIn(viewModelScope)
        }.addOnCompleteListener {
            fetchingState = false
            showSplashScreen = false
            if (!it.isSuccessful) {
                onFailure()
//                showErrorScreen = true
            }
        }
    }

    fun updateProfilePic(url: String, onResponse: () -> Unit, onFailure: () -> Unit) {
        updateUserJob?.cancel()
        updateUserJob = userUseCases.updateUser(user.copy(avatar = url)).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    user = resource.data
                    onResponse()
                }
                is Resource.Error -> onFailure()
            }
        }.launchIn(viewModelScope)
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
