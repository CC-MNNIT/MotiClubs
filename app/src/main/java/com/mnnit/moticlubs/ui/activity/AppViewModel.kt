package com.mnnit.moticlubs.ui.activity

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.api.UserDetailResponse
import com.mnnit.moticlubs.api.UserResponse
import com.mnnit.moticlubs.getAuthToken
import com.mnnit.moticlubs.setAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val application: Application) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    val showSplashScreen = mutableStateOf(true)
    val paddingValues = mutableStateOf(PaddingValues(0.dp))

    val name = mutableStateOf("")
    val email = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val regNo = mutableStateOf("")
    val course = mutableStateOf("")
    val avatar = mutableStateOf("")
    val adminList = mutableListOf<String>()
    val subscribedList = mutableListOf<String>()

    val adminInfoMap = mutableMapOf<String, UserDetailResponse>()

    val isAdmin
        get() = adminList.isNotEmpty()

    fun setUser(user: UserResponse) {
        name.value = user.name
        email.value = user.email
        phoneNumber.value = user.phoneNumber
        regNo.value = user.registrationNumber
        course.value = user.course
        avatar.value = user.avatar

        adminList.clear()
        user.admin.forEach { adminList.add(it) }

        subscribedList.clear()
        user.subscribed.forEach { subscribedList.add(it) }
    }

    fun getAuthToken(context: Context) = context.getAuthToken()

    fun setAuthToken(context: Context, token: String) {
        context.setAuthToken(token)
    }

    fun logoutUser(context: Context) {
        FirebaseAuth.getInstance().signOut()
        context.setAuthToken("")
    }

    init {
        FirebaseAuth.getInstance().addIdTokenListener { it: InternalTokenResult ->
            Log.d(TAG, "addIdTokenListener: called")
            application.setAuthToken(it.token ?: "")
            Log.d(TAG, "addIdTokenListener: saved")
        }
    }
}

object AppNavigation {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val CLUB_PAGE = "club_page"
    const val PROFILE = "profile"
    const val CONTACT_US = "contact_us"
}
