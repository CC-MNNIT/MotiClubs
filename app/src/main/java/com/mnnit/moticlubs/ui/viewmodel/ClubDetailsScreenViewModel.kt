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
import com.google.gson.Gson
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.ClubDetailModel
import com.mnnit.moticlubs.network.model.UpdateClubModel
import com.mnnit.moticlubs.network.model.UrlResponseModel
import com.mnnit.moticlubs.ui.components.OtherLinkComposeModel
import com.mnnit.moticlubs.ui.components.SocialLinkComposeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var clubModel by mutableStateOf(savedStateHandle.get<ClubDetailModel>("clubDetail") ?: ClubDetailModel())
    var isFetching by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    val showSocialLinkDialog = mutableStateOf(false)
    val showOtherLinkDialog = mutableStateOf(false)
    val showProgressDialog = mutableStateOf(false)
    val showColorPaletteDialog = mutableStateOf(false)

    val otherLinks = mutableStateListOf<UrlResponseModel>()
    val otherLinksLiveList = mutableStateListOf<OtherLinkComposeModel>()
    val otherLinkIdx = mutableStateOf(0)

    val socialLinksLiveList = mutableStateListOf(
        SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel()
    )
    val socialLinks = mutableStateListOf(
        UrlResponseModel(), UrlResponseModel(), UrlResponseModel(), UrlResponseModel()
    )

    var isAdmin = false

    fun pushUrls(
        _list: List<UrlResponseModel>,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit,
    ) {
        viewModelScope.launch {
            val clubID = clubModel.id
            val list = _list.map { it.mapToUrlModel() }
            Log.d("TAG", "pushUrls: ${Gson().toJson(list)}")
            val response = repository.pushUrls(application, clubID, list)
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun updateProfilePic(url: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val clubID = clubModel.id
            val response = repository.updateClub(
                application,
                clubID,
                UpdateClubModel(clubModel.description, url, clubModel.summary)
            )

            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun fetchUrls() {
        isFetching = true

        viewModelScope.launch {
            val clubID = clubModel.id
            val response = repository.getUrls(application, clubID)
            if (response is Success) {
                val urls = response.obj

                socialLinks[0] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("facebook")
                } ?: UrlResponseModel()
                socialLinks[1] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("instagram")
                } ?: UrlResponseModel()
                socialLinks[2] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("twitter")
                } ?: UrlResponseModel()
                socialLinks[3] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("github")
                } ?: UrlResponseModel()

                for (i in socialLinks.indices) {
                    socialLinksLiveList[i] = socialLinks[i].mapToSocialLinkModel()
                        .apply {
                            this.urlName = SocialLinkComposeModel.socialLinkNames[i]
                            this.clubID = clubModel.id
                        }
                }

                otherLinks.clear()
                otherLinks.addAll(urls.filter { f ->
                    !SocialLinkComposeModel.socialLinkNames.any { s -> f.name.contains(s) }
                })
                otherLinksLiveList.clear()
                otherLinksLiveList.addAll(otherLinks.map { m -> m.mapToOtherLinkModel() })
            } else {
                Toast.makeText(application, "${response.errCode}: Error couldn't load links", Toast.LENGTH_LONG).show()
            }
            isFetching = false
        }
    }

    init {
        fetchUrls()
    }
}
