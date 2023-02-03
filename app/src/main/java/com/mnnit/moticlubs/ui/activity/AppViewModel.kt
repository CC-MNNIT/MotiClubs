package com.mnnit.moticlubs.ui.activity

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.InternalTokenResult
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
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

    val showErrorScreen = mutableStateOf(false)
    val fetchingState = mutableStateOf(false)
    val showSplashScreen = mutableStateOf(true)
    val clubModel = mutableStateOf(ClubModel())

    val name = mutableStateOf("")
    val email = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val regNo = mutableStateOf("")
    val course = mutableStateOf("")
    val avatar = mutableStateOf("")
    val adminList = mutableListOf<String>()
    val subscribedList = mutableListOf<String>()
    val subscriberCount = mutableStateOf<Int>(0)
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

    fun fetchUser(user: FirebaseUser?, context: Context) {
        fetchingState.value = true
        if (user != null) {
            API.getUserData(getAuthToken(context), {
                setUser(it)
                fetchingState.value = false
                showErrorScreen.value = false
                showSplashScreen.value = false
                Log.d(TAG, "fetchUser")
            }) {
                fetchingState.value = false
                showErrorScreen.value = true
                showSplashScreen.value = false
                Log.d(TAG, "fetchUser: error: $it")
            }
        } else {
            fetchingState.value = false
            showSplashScreen.value = false
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

object AppNavigation {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val CLUB_PAGE = "club_page"
    const val PROFILE = "profile"
    const val CONTACT_US = "contact_us"
    const val POST_PAGE = "post_page"
    const val CLUB_DETAIL = "club_detail"
}
