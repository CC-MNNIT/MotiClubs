package com.mnnit.moticlubs.ui.activity

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.InternalTokenResult
import com.google.gson.Gson
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.*
import com.mnnit.moticlubs.setAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {

    companion object {
        private const val TAG = "AppViewModel"
    }

    var showErrorScreen by mutableStateOf(false)
    var fetchingState by mutableStateOf(false)
    var showSplashScreen by mutableStateOf(true)
    var adminMap = mutableStateMapOf<Int, AdminDetailResponse>()
    var user: UserResponse by mutableStateOf(UserResponse())

    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        application.setAuthToken("")
    }

    fun fetchUser(
        user: FirebaseUser?,
        onResponse: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        fetchingState = true
        if (user != null) {
            viewModelScope.launch {
                val response = withContext(Dispatchers.IO) { repository.getUserData(application) }

                fetchingState = false
                showSplashScreen = false
                if (response is Success) {
                    this@AppViewModel.user = response.obj
                    showErrorScreen = false
                    Log.d(TAG, "fetchUser")

                    onResponse()
                } else {
                    showErrorScreen = true
                    Log.d(TAG, "fetchUser: error: code: ${response.errCode}, msg: ${response.errMsg}")
                    onFailure()
                }
            }
        } else {
            fetchingState = false
            showSplashScreen = false
        }
    }

    fun fetchAllAdmins() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.getAllAdmins(application) }
            if (response is Success) {
                response.obj.forEach { model -> adminMap[model.uid] = model }
            }
        }
    }

    fun updateProfilePic(
        url: String,
        onResponse: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.setProfilePicUrl(application, url) }
            if (response is Success) {
                user.avatar = url
                onResponse()
            } else {
                onFailure()
            }
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

class ClubParamType : NavType<ClubDetailModel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): ClubDetailModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, ClubDetailModel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): ClubDetailModel =
        Gson().fromJson(value, ClubDetailModel::class.java)

    override fun put(bundle: Bundle, key: String, value: ClubDetailModel) {
        bundle.putParcelable(key, value)
    }
}

class ClubNavParamType : NavType<ClubNavModel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): ClubNavModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, ClubNavModel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): ClubNavModel =
        Gson().fromJson(value, ClubNavModel::class.java)

    override fun put(bundle: Bundle, key: String, value: ClubNavModel) {
        bundle.putParcelable(key, value)
    }
}

class PostParamType : NavType<PostNotificationModel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): PostNotificationModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, PostNotificationModel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): PostNotificationModel =
        Gson().fromJson(value, PostNotificationModel::class.java)

    override fun put(bundle: Bundle, key: String, value: PostNotificationModel) {
        bundle.putParcelable(key, value)
    }
}
