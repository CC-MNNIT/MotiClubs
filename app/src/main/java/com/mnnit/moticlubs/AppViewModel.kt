package com.mnnit.moticlubs

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {

    val showSplashScreen = mutableStateOf(true)
    val userPresent = mutableStateOf(false)

    init {
        viewModelScope.launch {
            delay(500L)
            showSplashScreen.value = false
        }
    }
}