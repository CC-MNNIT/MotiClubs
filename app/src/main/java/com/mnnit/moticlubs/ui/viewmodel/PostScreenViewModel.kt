package com.mnnit.moticlubs.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.mnnit.moticlubs.domain.model.View
import com.mnnit.moticlubs.domain.use_case.ViewUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PostScreenViewModel @Inject constructor(
    private val viewUseCases: ViewUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "PostScreenViewModel"
    }

    var viewCount by mutableStateOf("-")
    var postNotificationModel by mutableStateOf(
        savedStateHandle.get<PostNotificationModel>(NavigationArgs.POST_ARG) ?: PostNotificationModel()
    )

    private var viewPostJob: Job? = null
    private var getViewJob: Job? = null

    private fun viewPost() {
        viewPostJob?.cancel()
        viewPostJob = viewUseCases.addViews(View(postNotificationModel.userID, postNotificationModel.postID))
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> resource.data?.let { list -> viewCount = list.size.toString() }
                    is Resource.Success -> viewCount = resource.data.size.toString()
                    is Resource.Error -> Log.d(TAG, "viewPost: ${resource.errCode} : ${resource.errMsg}")
                }
            }.launchIn(viewModelScope)
    }

    private fun getViews() {
        getViewJob?.cancel()
        getViewJob = viewUseCases.getViews(postNotificationModel.postID).onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { list -> viewCount = list.size.toString() }
                is Resource.Success -> viewCount = resource.data.size.toString()
                is Resource.Error -> Log.d(TAG, "getViews: ${resource.errCode} : ${resource.errMsg}")
            }
        }.launchIn(viewModelScope)
    }

    init {
        viewPost()
        getViews()
    }
}
