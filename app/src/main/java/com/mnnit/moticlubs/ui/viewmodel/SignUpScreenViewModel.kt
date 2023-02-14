package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.SaveUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {

    val emailID = mutableStateOf("")
    val name = mutableStateOf("")
    val regNo = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val password = mutableStateOf("")

    val courseList = listOf("B.Tech", "M.Tech", "MBA", "MCA", "PhD")
    val selectedCourse = mutableStateOf("")

    val isPasswordVisible = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val isPasswordInvalid
        get() = password.value.isNotEmpty() && password.value.length <= 6

    val isSignUpButtonEnabled
        get() = !isLoading.value
                && !isPasswordInvalid
                && password.value.isNotEmpty()
                && emailID.value.isNotEmpty()
                && name.value.isNotEmpty()
                && regNo.value.isNotEmpty()
                && phoneNumber.value.isNotEmpty()
                && selectedCourse.value.isNotEmpty()

    val dropDownExpanded = mutableStateOf(false)

    fun resetState() {
        emailID.value = ""
        name.value = ""
        regNo.value = ""
        phoneNumber.value = ""
        password.value = ""
        selectedCourse.value = ""
        isPasswordVisible.value = false
        isLoading.value = false
        dropDownExpanded.value = false
    }

    fun saveUser(
        saveUserModel: SaveUserModel,
        onResponse: () -> Unit, onFailure: (code: Int) -> Unit
    ) {
        viewModelScope.launch {
            val response = repository.saveUser(application, saveUserModel)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
                Log.d("TAG", "saveUser: ${response.errMsg}")
            }
        }
    }
}
