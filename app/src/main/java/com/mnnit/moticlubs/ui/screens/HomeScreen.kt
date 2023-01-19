package com.mnnit.moticlubs.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.ui.activity.AppScreenMode
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.activity.ClubActivity
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel() {

    val clubsList = mutableStateListOf<ClubModel>()

    fun setClubsList(list: List<ClubModel>) {
        clubsList.clear()
        list.forEach { clubsList.add(it) }
    }
}

@Composable
fun HomeScreen(appViewModel: AppViewModel, viewModel: HomeScreenViewModel = hiltViewModel()) {
    API.getClubs(appViewModel.getAuthToken(LocalContext.current), {
        viewModel.setClubsList(it)
    }) {}

    val context = LocalContext.current

    MotiClubsTheme(getColorScheme()) {
        Surface(modifier = Modifier.fillMaxHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                ProfileIcon(appViewModel = appViewModel, modifier = Modifier.align(Alignment.End))

                Text(text = "MNNIT Clubs", fontSize = 28.sp)

                LazyColumn(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxHeight()
                ) {
                    items(viewModel.clubsList.size) { idx ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp), onClick = {
                                context.startActivity(Intent(context, ClubActivity::class.java).apply {
                                    putExtra(Constants.CLUB, viewModel.clubsList[idx])
                                })
                            },
                            shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Image(
                                    painter = rememberVectorPainter(image = Icons.Outlined.AccountCircle),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(48.dp)
                                        .align(Alignment.CenterVertically)
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
                                        modifier = Modifier.fillMaxWidth(0.6f)
                                    )
                                }

                                BadgedBox(badge = {
                                    Badge { Text(text = "10") }
                                }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileIcon(appViewModel: AppViewModel, modifier: Modifier) {
    if (appViewModel.avatar.value.isEmpty()) {
        Image(
            painter = rememberVectorPainter(image = Icons.Outlined.AccountCircle),
            contentDescription = "",
            modifier = modifier
                .clip(CircleShape)
                .size(48.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    appViewModel.appScreenMode.value = AppScreenMode.LOGIN
                }
        )
    } else {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(appViewModel.avatar.value)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(Constants.AVATAR)
                    .placeholder(com.mnnit.moticlubs.R.drawable.outline_account_circle_24)
                    .build()
            ),
            contentDescription = "",
            modifier = modifier
                .clip(CircleShape)
                .size(48.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    appViewModel.appScreenMode.value = AppScreenMode.LOGIN
                }
        )
    }
}