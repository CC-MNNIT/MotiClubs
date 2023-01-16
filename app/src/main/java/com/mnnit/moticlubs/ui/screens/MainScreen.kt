package com.mnnit.moticlubs.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

    val screenMode = mutableStateOf(MainScreenMode.HOME)
}

enum class MainScreenMode {
    HOME, PROFILE, CONTACT
}

@Composable
fun MainScreen(
    appViewModel: AppViewModel,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    MotiClubsTheme(getColorScheme()) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)

        Scaffold(
            modifier = Modifier,
            content = {
                AnimatedVisibility(
                    visible = viewModel.screenMode.value == MainScreenMode.HOME,
                    enter = fadeIn(), exit = fadeOut(),
                    modifier = Modifier.padding(it)
                ) {
                    HomeScreen(appViewModel)
                }

                AnimatedVisibility(
                    visible = viewModel.screenMode.value == MainScreenMode.PROFILE,
                    enter = fadeIn(), exit = fadeOut(),
                    modifier = Modifier.padding(it)
                ) {
                    ProfileScreen()
                }

                AnimatedVisibility(
                    visible = viewModel.screenMode.value == MainScreenMode.CONTACT,
                    enter = fadeIn(), exit = fadeOut(),
                    modifier = Modifier.padding(it)
                ) {
                    ContactUsScreen()
                }
            },
            bottomBar = { BottomBar(viewModel = viewModel) }
        )
    }
}

@Composable
private fun BottomBar(viewModel: MainScreenViewModel) {
    NavigationBar(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(56.dp))
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    if (viewModel.screenMode.value == MainScreenMode.HOME) {
                        Icons.Filled.Home
                    } else {
                        Icons.Outlined.Home
                    }, ""
                )
            },
            label = { Text(text = "Home", fontSize = 14.sp) },
            selected = viewModel.screenMode.value == MainScreenMode.HOME,
            onClick = { viewModel.screenMode.value = MainScreenMode.HOME }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (viewModel.screenMode.value == MainScreenMode.PROFILE) {
                        Icons.Filled.AccountCircle
                    } else {
                        Icons.Outlined.AccountCircle
                    }, ""
                )
            },
            label = { Text(text = "Profile", fontSize = 14.sp) },
            selected = viewModel.screenMode.value == MainScreenMode.PROFILE,
            onClick = { viewModel.screenMode.value = MainScreenMode.PROFILE }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (viewModel.screenMode.value == MainScreenMode.CONTACT) {
                        Icons.Filled.Help
                    } else {
                        Icons.Outlined.HelpOutline
                    }, ""
                )
            },
            label = { Text(text = "Contact Us", fontSize = 14.sp) },
            selected = viewModel.screenMode.value == MainScreenMode.CONTACT,
            onClick = { viewModel.screenMode.value = MainScreenMode.CONTACT }
        )
    }
}