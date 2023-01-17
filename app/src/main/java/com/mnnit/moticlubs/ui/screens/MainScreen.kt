package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = viewModel.screenMode.value == MainScreenMode.HOME,
                        enter = fadeIn(), exit = fadeOut(),
                        modifier = Modifier.padding(it)
                    ) { HomeScreen(appViewModel) }

                    AnimatedVisibility(
                        visible = viewModel.screenMode.value == MainScreenMode.PROFILE,
                        enter = fadeIn(), exit = fadeOut(),
                        modifier = Modifier.padding(it)
                    ) { ProfileScreen() }

                    AnimatedVisibility(
                        visible = viewModel.screenMode.value == MainScreenMode.CONTACT,
                        enter = fadeIn(), exit = fadeOut(),
                        modifier = Modifier.padding(it)
                    ) { ContactUsScreen() }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = "Contact Us",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = ""
                        )
                    },
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(24.dp)
                )
            }
        )
    }
}
