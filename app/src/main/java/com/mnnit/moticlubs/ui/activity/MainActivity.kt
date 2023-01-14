package com.mnnit.moticlubs.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.mnnit.moticlubs.ui.theme.getColorScheme
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
                true -> viewModel.appScreenMode.value = AppScreenMode.MAIN
                else -> viewModel.appScreenMode.value = AppScreenMode.LOGIN
            }

            MotiClubsTheme(getColorScheme(context = this)) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    val systemUiController = rememberSystemUiController()
                    systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)

                    AnimatedVisibility(
                        visible = viewModel.appScreenMode.value == AppScreenMode.LOGIN,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        LoginScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                    AnimatedVisibility(
                        visible = viewModel.appScreenMode.value == AppScreenMode.SIGNUP,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        SignupScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                    AnimatedVisibility(
                        visible = viewModel.appScreenMode.value == AppScreenMode.MAIN,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        MainScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                }
            }
        }
    }
}
