package com.mnnit.moticlubs.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.PostNotificationModel
import com.mnnit.moticlubs.postRead
import com.mnnit.moticlubs.ui.components.MarkdownText
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
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

@Composable
fun PostScreen(
    viewModel: PostScreenViewModel = hiltViewModel()
) {
    LocalContext.current.postRead(
        viewModel.postNotificationModel.channelID,
        viewModel.postNotificationModel.postID,
        true
    )

    val colorScheme = getColorScheme()
    val scroll = rememberScrollState(0)

    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(2.dp, false)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                    shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Text(
                        "${viewModel.postNotificationModel.clubName} - ${viewModel.postNotificationModel.channelName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ProfilePicture(url = viewModel.postNotificationModel.adminAvatar, size = 56.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        AdminNameTimestamp(
                            time = viewModel.postNotificationModel.time,
                            name = viewModel.postNotificationModel.adminName
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 16.dp, bottom = 16.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.Visibility, contentDescription = "")
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = viewModel.viewCount,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                MarkdownText(
                    markdown = viewModel.postNotificationModel.message,
                    color = contentColorFor(backgroundColor = getColorScheme().background),
                    selectable = true,
                    disableLinkMovementMethod = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminNameTimestamp(time: String, name: String) {
    Column(modifier = Modifier
        .padding(start = 8.dp)
        .semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
