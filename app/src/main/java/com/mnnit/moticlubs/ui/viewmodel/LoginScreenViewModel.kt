package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.FCMTokenDto
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.apiInvoker
import com.mnnit.moticlubs.domain.util.getAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val application: Application,
    private val apiService: ApiService
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
            val bodyResource = apiInvoker { apiService.setFCMToken(application.getAuthToken(), FCMTokenDto(token)) }
            if (bodyResource is Resource.Success) {
                onSuccess()
            } else {
                onFailure(bodyResource.errorCode)
            }
        }
    }
}
