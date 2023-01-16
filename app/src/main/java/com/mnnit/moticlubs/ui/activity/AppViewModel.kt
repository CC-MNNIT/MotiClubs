package com.mnnit.moticlubs.ui.activity

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.api.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val application: Application) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    val showSplashScreen = mutableStateOf(true)
    val userPresent = mutableStateOf(false)

    val appScreenMode = mutableStateOf(AppScreenMode.INVALID)

    val name = mutableStateOf("")
    val email = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val regNo = mutableStateOf("")
    val course = mutableStateOf("")
    val avatar = mutableStateOf("")
    val adminList = mutableListOf<String>()
    val subscribedList = mutableListOf<String>()

    val isAdmin
        get() = adminList.isNotEmpty()

    fun setUser(user: UserResponse) {
        name.value = user.name
        email.value = user.email
        phoneNumber.value = user.phoneNumber
        regNo.value = user.registrationNumber
        course.value = user.course
        avatar.value = user.avatar
        user.admin.forEach { adminList.add(it) }
        user.subscribed.forEach { subscribedList.add(it) }
    }

    fun getAuthToken(context: Context) =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .getString(Constants.TOKEN, "")

    fun setAuthToken(context: Context, token: String) {
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putString(Constants.TOKEN, token).apply()
    }

    init {
        viewModelScope.launch {
            showSplashScreen.value = false
        }
        FirebaseAuth.getInstance().addIdTokenListener { it: InternalTokenResult ->
            Log.d(TAG, "addIdTokenListener: called")
            setAuthToken(application.applicationContext, it.token ?: "")
            Log.d(TAG, "addIdTokenListener: saved")
        }
    }
}

enum class AppScreenMode {
    INVALID, LOGIN, SIGNUP, MAIN
}
