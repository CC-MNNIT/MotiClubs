@file:OptIn(ExperimentalLayoutApi::class)

package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.api.UserDetailResponse
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    val clubsList = mutableStateListOf<ClubModel>()

    fun setClubsList(context: Context, appViewModel: AppViewModel) {
        API.getClubs(context.getAuthToken(), { list ->
            clubsList.clear()
            val hash = HashMap<String, UserDetailResponse>()

            list.forEach { model ->
                clubsList.add(model)

                model.admins.forEach { email ->
                    if (!hash.containsKey(email)) {
                        Log.d("TAG", "HomeScreen: Fetching $email")
                        hash[email] = UserDetailResponse()

                        appViewModel.viewModelScope.launch {
                            API.getUserDetails(context.getAuthToken(), email, { adminRes ->
                                appViewModel.adminInfoMap[email] = adminRes
                            }) {}
                        }
                    }
                }
            }
        }) {}
    }
}

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    onNavigatePostItemClick: (club: ClubModel) -> Unit,
    onNavigateContactUs: () -> Unit,
    onNavigateProfile: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.setClubsList(context, appViewModel)

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()
        Scaffold(
            modifier = Modifier,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(it)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    ProfileIcon(
                        appViewModel = appViewModel,
                        modifier = Modifier.align(Alignment.End),
                        onNavigateProfile
                    )

                    Text(text = "MNNIT Clubs", fontSize = 28.sp)

                    AnimatedVisibility(visible = viewModel.clubsList.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
                        ClubList(viewModel = viewModel, onNavigatePostItemClick = onNavigatePostItemClick)
                    }
                    AnimatedVisibility(visible = viewModel.clubsList.isEmpty(), modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = "Contact Us", fontSize = 15.sp, textAlign = TextAlign.Center) },
                    icon = { Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = "") },
                    onClick = { onNavigateContactUs() },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding()
                )
            }
        )
    }
}

@Composable
fun ClubList(viewModel: HomeScreenViewModel, onNavigatePostItemClick: (club: ClubModel) -> Unit) {
    val colorScheme = getColorScheme()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxHeight(),
        contentPadding = PaddingValues()
    ) {
        items(viewModel.clubsList.size) { idx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), onClick = {
                    onNavigatePostItemClick(viewModel.clubsList[idx])
                },
                shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically),
                        painter = if (viewModel.clubsList[idx].avatar.isEmpty() || !viewModel.clubsList[idx].avatar.matches(
                                Patterns.WEB_URL.toRegex()
                            )
                        ) {
                            rememberVectorPainter(image = Icons.Outlined.AccountCircle)
                        } else {
                            rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(viewModel.clubsList[idx].avatar)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(Constants.AVATAR)
                                    .placeholder(R.drawable.outline_account_circle_24)
                                    .build()
                            )
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth(0.9f)
                    ) {
                        Text(text = viewModel.clubsList[idx].name, fontSize = 16.sp)
                        Text(
                            text = viewModel.clubsList[idx].description,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            softWrap = true,
                            maxLines = 2
                        )
                    }

                    AnimatedVisibility(
                        visible = context.getUnreadPost(viewModel.clubsList[idx].id).isNotEmpty(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        BadgedBox(badge = {
                            Badge { Text(text = "${context.getUnreadPost(viewModel.clubsList[idx].id).size}") }
                        }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileIcon(
    appViewModel: AppViewModel, modifier: Modifier,
    onNavigateProfile: () -> Unit
) {
    Image(
        painter = LocalContext.current.getImageUrlPainter(url = appViewModel.avatar.value), contentDescription = "",
        modifier = modifier
            .clip(CircleShape)
            .size(48.dp)
            .clickable { onNavigateProfile() }
    )
}