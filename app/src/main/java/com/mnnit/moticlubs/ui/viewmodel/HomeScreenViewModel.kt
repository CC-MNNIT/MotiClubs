package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.Repository
import com.mnnit.moticlubs.data.network.Success
import com.mnnit.moticlubs.data.network.model.AddChannelDto
import com.mnnit.moticlubs.data.network.model.ClubModel
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

    fun addChannel() {
        showAddChannelDialog = false
        progressMsg = "Adding"
        showProgressDialog = true

        viewModelScope.launch {
            val response = repository.createChannel(application, AddChannelDto(clubID, inputChannel))
            if (response is Success) {
                showProgressDialog = false
                fetchClubsList()
                Toast.makeText(application, "Channel Added", Toast.LENGTH_SHORT).show()
            } else {
                showProgressDialog = false
                Toast.makeText(application, "${response.errCode}: Error adding channel", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateChannel() {
        showUpdateChannelDialog = false
        progressMsg = "Updating"
        showProgressDialog = true

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repository.updateChannelName(application, channelID, updateChannel)
            }
            if (response is Success) {
                showProgressDialog = false
                fetchClubsList()
                Toast.makeText(application, "Channel Updated", Toast.LENGTH_SHORT).show()
            } else {
                showProgressDialog = false
                Toast.makeText(application, "${response.errCode}: Error updating channel", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteChannel() {
        showUpdateChannelDialog = false
        progressMsg = "Deleting"
        showProgressDialog = true

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repository.deleteChannel(application, channelID)
            }
            if (response is Success) {
                showProgressDialog = false
                fetchClubsList()
                Toast.makeText(application, "Channel Deleted", Toast.LENGTH_SHORT).show()
            } else {
                showProgressDialog = false
                Toast.makeText(application, "${response.errCode}: Error deleting channel", Toast.LENGTH_SHORT).show()
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
