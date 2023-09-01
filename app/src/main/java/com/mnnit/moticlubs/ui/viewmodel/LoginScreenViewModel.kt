package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.FCMTokenDto
import com.mnnit.moticlubs.data.network.dto.SaveUserDto
import com.mnnit.moticlubs.domain.model.Stamp
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.ResponseStamp
import com.mnnit.moticlubs.domain.util.apiInvoker
import com.mnnit.moticlubs.domain.util.getAuthToken
import com.mnnit.moticlubs.domain.util.setAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val application: Application,
    private val apiService: ApiService,
    private val repository: Repository,
) : ViewModel() {

    companion object {
        private const val TAG = "LoginScreenViewModel"
    }

    val isLoading = mutableStateOf(false)

    private fun setFCMToken(token: String, onSuccess: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val bodyResource = apiInvoker { apiService.setFCMToken(application.getAuthToken(), FCMTokenDto(token)) }
            if (bodyResource is Resource.Success) {
                onSuccess()
            } else {
                onFailure(bodyResource.errorCode)
            }
        }
    }

    private fun saveUser(
        saveUserDto: SaveUserDto,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val stamp = (repository.getStampByKey(ResponseStamp.USER.getKey())
                ?: Stamp(ResponseStamp.USER.getKey(), 0)).stamp
            val bodyResource = apiInvoker {
                apiService.saveUser(application.getAuthToken(), stamp, saveUserDto)
            }
            if (bodyResource is Resource.Success) {
                onResponse()
            } else {
                onFailure(bodyResource.errorCode)
                Log.d(TAG, "saveUser: ${bodyResource.errorMsg}")
            }
        }
    }

    fun login(
        context: Context,
        credential: AuthCredential,
        preUser: SaveUserDto,
        appViewModel: AppViewModel,
        onNavigateToMain: () -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    isLoading.value = false
                    Toast.makeText(context, task.exception?.message ?: "Login failure", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    isLoading.value = false
                    Toast.makeText(context, "Error: Could not login", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    return@addOnCompleteListener
                }

                if (user.isEmailVerified) {
                    Log.d(TAG, "login: FirebaseIDToken not invoked. Fetching token")
                    user.getIdToken(false).addOnSuccessListener { result ->
                        val token = result.token
                        if (token == null) {
                            auth.signOut()
                            isLoading.value = false
                            Toast.makeText(context, "Error: Couldn't init session", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }
                        context.setAuthToken(token)

                        val userId = result.claims["userId"]?.toString()?.toLong() ?: -1
                        Log.d(TAG, "login: userID: $userId")
                        if (userId == -1L) {
                            Log.d(TAG, "login: userId claim null - saving $preUser")
                            saveUser(preUser, {
                                auth.signOut()
                                login(context, credential, preUser, appViewModel, onNavigateToMain)
                            }) {
                                auth.signOut()
                                isLoading.value = false
                                Toast.makeText(context, "$it: Error signing up", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            handleUser(context, auth, appViewModel, onNavigateToMain)
                        }
                    }
                } else {
                    auth.signOut()
                    isLoading.value = false
                    Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleUser(
        context: Context,
        auth: FirebaseAuth,
        appViewModel: AppViewModel,
        onNavigateToMain: () -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcm ->
            setFCMToken(fcm, {
                appViewModel.getUser(auth.currentUser, {
                    isLoading.value = false
                    onNavigateToMain()
                }) {
                    auth.signOut()
                    isLoading.value = false
                    Toast.makeText(context, "Error: Couldn't load user", Toast.LENGTH_SHORT).show()
                }
            }) {
                auth.signOut()
                isLoading.value = false
                Toast.makeText(context, "Error: Couldn't set db-msg token", Toast.LENGTH_SHORT).show()
            }
        }.addOnCompleteListener {
            if (!it.isSuccessful) {
                auth.signOut()
                isLoading.value = false
                Toast.makeText(context, "Error: Couldn't set msg token", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
