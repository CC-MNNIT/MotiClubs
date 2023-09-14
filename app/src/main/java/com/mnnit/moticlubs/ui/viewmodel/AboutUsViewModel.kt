package com.mnnit.moticlubs.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.GithubContributorDto
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.apiInvoker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val apiService: ApiService,
) : ViewModel() {

    companion object {
        private const val TAG = "AboutUsViewModel"
    }

    var loadingContributors by mutableStateOf(false)
    var showContributorDialog by mutableStateOf(false)
    var contributorTagApp by mutableStateOf(true)

    val appContributors = mutableStateListOf<GithubContributorDto>()
    val backendContributors = mutableStateListOf<GithubContributorDto>()

    fun getContributors() {
        viewModelScope.launch {
            loadingContributors = true

            if (contributorTagApp) {
                getResource(apiInvoker { apiService.getAppContributors() }) {
                    appContributors.clear()
                    appContributors.addAll(it)
                    loadingContributors = false
                }
            } else {
                getResource(apiInvoker { apiService.getBackendContributors() }) {
                    backendContributors.clear()
                    backendContributors.addAll(it)
                    loadingContributors = false
                }
            }
        }
    }

    private fun getResource(
        resource: Resource<Pair<List<GithubContributorDto>?, Long>>,
        onSuccess: (list: List<GithubContributorDto>) -> Unit
    ) {
        when (resource) {
            is Resource.Error -> Log.d(TAG, "getResource: ${resource.errCode}: ${resource.errMsg}")
            is Resource.Loading -> {}
            is Resource.Success -> {
                val result = resource.data.first
                if (result != null) {
                    onSuccess(result.sortedByDescending { it.contributions })
                }
            }
        }
    }
}
