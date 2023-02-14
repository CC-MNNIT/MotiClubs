package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.PostNotificationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PostScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var viewCount by mutableStateOf("-")
    var postNotificationModel by mutableStateOf(
        savedStateHandle.get<PostNotificationModel>("post") ?: PostNotificationModel()
    )

    private fun viewPost() {
        viewModelScope.launch {
            val postID = postNotificationModel.postID
            withContext(Dispatchers.IO) { repository.addViews(application, postID) }
        }
    }

    private fun getViews() {
        viewModelScope.launch {
            val postID = postNotificationModel.postID
            val response = withContext(Dispatchers.IO) { repository.getViews(application, postID) }
            if (response is Success) {
                viewCount = "${response.obj.count}"
            }
        }
    }

    init {
        viewPost()
        getViews()
    }
}
