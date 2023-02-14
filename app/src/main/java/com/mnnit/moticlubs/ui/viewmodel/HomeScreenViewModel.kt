package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.AddChannelDto
import com.mnnit.moticlubs.network.model.ClubModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {

    val clubsList = mutableStateListOf<ClubModel>()
    var isFetching by mutableStateOf(false)

    var showAddChannelDialog by mutableStateOf(false)
    var showUpdateChannelDialog by mutableStateOf(false)
    var showProgressDialog by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    var clubID by mutableStateOf(-1)
    var channelID by mutableStateOf(-1)
    var inputChannel by mutableStateOf("")
    var updateChannel by mutableStateOf("")

    fun addChannel(onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = repository.createChannel(application, AddChannelDto(clubID, inputChannel))
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun updateChannel(onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repository.updateChannelName(application, channelID, updateChannel)
            }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun deleteChannel(onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repository.deleteChannel(application, channelID)
            }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun fetchClubsList() {
        isFetching = true
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { repository.getClubs(application) }
            if (response is Success) {
                clubsList.clear()
                clubsList.addAll(response.obj)
            }
            isFetching = false
        }
    }

    init {
        fetchClubsList()
    }
}
