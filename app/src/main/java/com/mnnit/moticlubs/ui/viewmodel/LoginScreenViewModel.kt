package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {

    val emailID = mutableStateOf("")
    val password = mutableStateOf("")

    val isPasswordVisible = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val isPasswordInvalid
        get() = password.value.isNotEmpty() && password.value.length <= 6

    val isLoginButtonEnabled
        get() = !isLoading.value
                && !isPasswordInvalid
                && password.value.isNotEmpty()
                && emailID.value.isNotEmpty()

    fun resetState() {
        emailID.value = ""
        password.value = ""
        isPasswordVisible.value = false
        isLoading.value = false
    }

    fun setFCMToken(token: String, onSuccess: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val fcmResponse = withContext(Dispatchers.IO) { repository.setFCMToken(application, token) }
            if (fcmResponse is Success) {
                onSuccess()
            } else {
                onFailure(fcmResponse.errCode)
            }
        }
    }
}
