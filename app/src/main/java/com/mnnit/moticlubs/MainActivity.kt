package com.mnnit.moticlubs

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
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen.value }

        setContent {
            viewModel.userPresent.value = FirebaseAuth.getInstance().currentUser != null
//            viewModel.removeSplash()

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

                    AnimatedVisibility(visible = !viewModel.userPresent.value) {
                        LoginScreen(context = this@MainActivity, appViewModel = viewModel)
                    }
                }
            }

//            MotiClubsTheme(
//                if (isSystemInDarkTheme()) {
//                    dynamicDarkColorScheme(this)
//                } else dynamicLightColorScheme(this)
//            ) {
//                // A surface container using the 'background' color from the theme
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    val systemUiController = rememberSystemUiController()
//                    systemUiController.setSystemBarsColor(
//                        color = MaterialTheme.colorScheme.background
//                    )
//                    Text(text = "Hello Android !")
//                }
//            }
        }
    }
}
