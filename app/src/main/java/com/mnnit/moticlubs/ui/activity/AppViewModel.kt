package com.mnnit.moticlubs.ui.activity

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.Constants
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    val showSplashScreen = mutableStateOf(true)
    val userPresent = mutableStateOf(false)

    val appScreenMode = mutableStateOf(AppScreenMode.LOGIN)

    fun getAuthToken(context: Context) =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .getString(Constants.TOKEN, "")

    fun setAuthToken(context: Context, token: String) {
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putString(Constants.TOKEN, token).apply()
    }

    fun setAuthListener(context: Context) {
        FirebaseAuth.getInstance().addIdTokenListener { it: InternalTokenResult ->
            Log.d(TAG, "addIdTokenListener: called")
            setAuthToken(context, it.token ?: "")
            Log.d(TAG, "addIdTokenListener: saved")
        }
    }

    init {
        viewModelScope.launch {
            delay(500L)
            showSplashScreen.value = false
        }
    }
}

enum class AppScreenMode {
    LOGIN, SIGNUP, MAIN
}
