package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.UrlUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.OtherLinkComposeModel
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.SocialLinkComposeModel
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.getUserID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor(
    private val application: Application,
    private val urlUseCases: UrlUseCases,
    private val clubUseCases: ClubUseCases,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "ClubDetailsScreenViewModel"
    }

    val clubId by mutableLongStateOf(savedStateHandle.getLongArg(NavigationArgs.CLUB_ARG))
    var userId by mutableLongStateOf(-1)

    var clubModel by mutableStateOf(Club())

    var isAdmin by mutableStateOf(false)

    var isFetching by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    var editDescriptionMode by mutableStateOf(false)
    var displayedDescription by mutableStateOf("...")

    val showSocialLinkDialog = mutableStateOf(false)
    val showOtherLinkDialog = mutableStateOf(false)
    val showProgressDialog = mutableStateOf(false)
    val showColorPaletteDialog = mutableStateOf(false)

    val otherLinks = mutableStateListOf<Url>()
    val otherLinksLiveList = mutableStateListOf<OtherLinkComposeModel>()
    val otherLinkIdx = mutableIntStateOf(0)

    val socialLinksLiveList = mutableStateListOf(
        SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel()
    )
    val socialLinks = mutableStateListOf(Url(), Url(), Url(), Url())

    private var getUrlsJob: Job? = null
    private var addUrlsJob: Job? = null
    private var updateClubJob: Job? = null
    private var getClubJob: Job? = null

    private fun getClub(loadLocal: Boolean = true) {
        getUrls()

        if (loadLocal) {
            Log.d(TAG, "getClub: loadLocal")
            viewModelScope.launch {
                clubModel = repository.getClub(clubId)
                displayedDescription = clubModel.description

                userId = application.getUserID()
                isAdmin = repository.getAdmins().any { admin -> admin.userId == userId && admin.clubId == clubId }
            }
            return
        }

        getClubJob?.cancel()
        getClubJob = clubUseCases.getClubs().onEach { resource ->
            when (resource) {
                is Resource.Loading -> resource.data?.let { list ->
                    clubModel = list.find { it.clubId == clubId } ?: Club()
                }

                is Resource.Success -> clubModel = resource.data.find { it.clubId == clubId } ?: Club()
                is Resource.Error -> {
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        getClub(loadLocal = false)
    }

    fun pushUrls(list: List<UrlModel>) {
        progressMsg = "Updating"
        showProgressDialog.value = true
        showSocialLinkDialog.value = false
        showOtherLinkDialog.value = false

        addUrlsJob?.cancel()
        addUrlsJob = urlUseCases.addUrls(clubId, list)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isFetching = true
                        resource.data?.let { list -> mapUrlList(list) }
                    }

                    is Resource.Success -> {
                        isFetching = false
                        showProgressDialog.value = false
                        mapUrlList(resource.data)
                        Toast.makeText(application, "Links updated", Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        isFetching = false
                        showProgressDialog.value = false
                        Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun updateClub(
        url: String = clubModel.avatar,
        description: String = clubModel.description,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit
    ) {
        updateClubJob?.cancel()
        updateClubJob = clubUseCases.updateClub(clubModel.copy(avatar = url, description = description))
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isFetching = true
                        resource.data?.let { model ->
                            clubModel = model
                            displayedDescription = clubModel.description
                        }
                    }

                    is Resource.Success -> {
                        isFetching = false
                        clubModel = resource.data
                        displayedDescription = clubModel.description
                        onResponse()
                    }

                    is Resource.Error -> {
                        isFetching = false
                        onFailure(resource.errCode)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getUrls() {
        isFetching = true

        getUrlsJob?.cancel()
        getUrlsJob = urlUseCases.getUrls(clubId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isFetching = true
                    resource.data?.let { list -> mapUrlList(list) }
                }

                is Resource.Success -> {
                    isFetching = false
                    mapUrlList(resource.data)
                }

                is Resource.Error -> {
                    isFetching = false
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun mapUrlList(urls: List<Url>) {
        socialLinks[0] = urls.findLast {
            it.name.toLowerCase(LocaleList.current).contains("facebook")
        } ?: Url()
        socialLinks[1] = urls.findLast {
            it.name.toLowerCase(LocaleList.current).contains("instagram")
        } ?: Url()
        socialLinks[2] = urls.findLast {
            it.name.toLowerCase(LocaleList.current).contains("twitter")
        } ?: Url()
        socialLinks[3] = urls.findLast {
            it.name.toLowerCase(LocaleList.current).contains("github")
        } ?: Url()

        for (i in socialLinks.indices) {
            socialLinksLiveList[i] = socialLinks[i].mapToSocialLinkModel().apply {
                this.urlName = SocialLinkComposeModel.socialLinkNames[i]
                this.clubID = clubId
            }
        }

        otherLinks.clear()
        otherLinks.addAll(urls.filter { f ->
            !SocialLinkComposeModel.socialLinkNames.any { s -> f.name.contains(s) }
        })
        otherLinksLiveList.clear()
        otherLinksLiveList.addAll(otherLinks.map { m -> m.mapToOtherLinkModel() })
    }

    init {
        getClub()
    }
}
