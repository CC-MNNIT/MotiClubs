package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
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
import com.mnnit.moticlubs.domain.model.Subscriber
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.use_case.ClubUseCases
import com.mnnit.moticlubs.domain.use_case.SubscriberUseCases
import com.mnnit.moticlubs.domain.use_case.UrlUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.ui.components.OtherLinkComposeModel
import com.mnnit.moticlubs.ui.components.SocialLinkComposeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor(
    private val application: Application,
    private val urlUseCases: UrlUseCases,
    private val clubUseCases: ClubUseCases,
    private val subscriberUseCases: SubscriberUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var clubModel by mutableStateOf(savedStateHandle.get<Club>(NavigationArgs.CLUB_ARG) ?: Club())
    private var userModel by mutableStateOf(savedStateHandle.get<User>(NavigationArgs.USER_ARG) ?: User())

    val subscriberList = mutableStateListOf<Subscriber>()
    var isAdmin by mutableStateOf(false)

    var isFetching by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    val showSocialLinkDialog = mutableStateOf(false)
    val showOtherLinkDialog = mutableStateOf(false)
    val showProgressDialog = mutableStateOf(false)
    val showColorPaletteDialog = mutableStateOf(false)

    val otherLinks = mutableStateListOf<Url>()
    val otherLinksLiveList = mutableStateListOf<OtherLinkComposeModel>()
    val otherLinkIdx = mutableStateOf(0)

    val socialLinksLiveList = mutableStateListOf(
        SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel()
    )
    val socialLinks = mutableStateListOf(Url(), Url(), Url(), Url())

    private var getUrlsJob: Job? = null
    private var addUrlsJob: Job? = null
    private var updateClubJob: Job? = null
    private var getSubscribersJob: Job? = null

    private fun getAdmins() {
        viewModelScope.launch {
            val resource = clubUseCases.getAdmins(shouldFetch = false).first()
            if (resource is Resource.Error) {
                return@launch
            }

            resource.d?.let { list ->
                val admins = list.filter { admin -> admin.clubID == clubModel.clubID }
                isAdmin = admins.any { admin -> admin.userID == userModel.userID }
            }
        }
    }

    private fun getSubscribers() {
        getSubscribersJob?.cancel()
        getSubscribersJob = subscriberUseCases.getSubscribers(clubModel.clubID).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        subscriberList.clear()
                        subscriberList.addAll(list)
                    }
                }
                is Resource.Success -> {
                    subscriberList.clear()
                    subscriberList.addAll(resource.data)

                    Log.d("TAG", "getSubscribers: ${subscriberList.size}")
                }
                is Resource.Error -> {
                    Log.d("TAG", "getSubscribers: error: ${resource.errCode} : ${resource.errMsg}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun pushUrls(list: List<UrlModel>) {
        progressMsg = "Updating"
        showProgressDialog.value = true
        showSocialLinkDialog.value = false
        showOtherLinkDialog.value = false

        addUrlsJob?.cancel()
        addUrlsJob = urlUseCases.addUrls(clubID = clubModel.clubID, list)
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

    fun updateProfilePic(url: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        updateClubJob?.cancel()
        updateClubJob = clubUseCases.updateClub(clubModel.copy(avatar = url)).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    isFetching = true
                    resource.data?.let { model -> clubModel = model }
                }
                is Resource.Success -> {
                    isFetching = false
                    clubModel = resource.data
                    onResponse()
                }
                is Resource.Error -> {
                    isFetching = false
                    onFailure(resource.errCode)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUrls() {
        isFetching = true

        getUrlsJob?.cancel()
        getUrlsJob = urlUseCases.getUrls(clubID = clubModel.clubID).onEach { resource ->
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
                this.clubID = clubModel.clubID
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
        getAdmins()
        getSubscribers()
        getUrls()
    }
}
