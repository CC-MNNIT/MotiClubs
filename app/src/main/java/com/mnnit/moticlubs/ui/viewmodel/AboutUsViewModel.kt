package com.mnnit.moticlubs.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.GithubContributorDto
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.apiInvoker
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.publishedStateListOf
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
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

    var loadingContributors by publishedStateOf(false)
    var showContributorDialog by publishedStateOf(false)
    var contributorTagApp by publishedStateOf(true)

    val appContributors = publishedStateListOf<GithubContributorDto>()
    val backendContributors = publishedStateListOf<GithubContributorDto>()

    fun getContributors() {
        viewModelScope.launch {
            loadingContributors = true

            if (contributorTagApp) {
                getResource(apiInvoker { apiService.getAppContributors() }) {
                    appContributors.value.clear()
                    appContributors.value.addAll(it)
                    loadingContributors = false
                }
            } else {
                getResource(apiInvoker { apiService.getBackendContributors() }) {
                    backendContributors.value.clear()
                    backendContributors.value.addAll(it)
                    loadingContributors = false
                }
            }
        }
    }

    private fun getResource(
        resource: Resource<Pair<List<GithubContributorDto>?, Long>>,
        onSuccess: (list: List<GithubContributorDto>) -> Unit,
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
