package com.mnnit.moticlubs.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.ui.screens.LoginScreen
import com.mnnit.moticlubs.ui.screens.MainScreen
import com.mnnit.moticlubs.ui.screens.SignupScreen
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen.value }

        viewModel.setAuthListener(this)

        setContent {
            viewModel.userPresent.value = FirebaseAuth.getInstance().currentUser != null
            when (viewModel.userPresent.value) {
                true -> {
                    viewModel.appScreenMode.value = AppScreenMode.MAIN
                    viewModel.mainScreenMode.value = MainScreenMode.HOME
                }
                else -> {
                    viewModel.appScreenMode.value = AppScreenMode.LOGIN
                    viewModel.mainScreenMode.value = MainScreenMode.INVALID
                }
            }

            MotiClubsTheme(
                if (isSystemInDarkTheme()) {
                    dynamicDarkColorScheme(this)
                } else dynamicLightColorScheme(this)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    val systemUiController = rememberSystemUiController()
                    systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)

                    AnimatedVisibility(
                        visible = viewModel.appScreenMode.value == AppScreenMode.LOGIN
                                && viewModel.mainScreenMode.value == MainScreenMode.INVALID
                    ) {
                        LoginScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                    AnimatedVisibility(
                        visible = viewModel.appScreenMode.value == AppScreenMode.MAIN
                                && viewModel.mainScreenMode.value == MainScreenMode.HOME
                    ) {
                        MainScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                }
            }
        }
    }
}
