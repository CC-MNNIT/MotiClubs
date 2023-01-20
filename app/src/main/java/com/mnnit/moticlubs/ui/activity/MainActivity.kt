package com.mnnit.moticlubs.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.ui.screens.ClubScreen
import com.mnnit.moticlubs.ui.screens.HomeScreen
import com.mnnit.moticlubs.ui.screens.LoginScreen
import com.mnnit.moticlubs.ui.screens.SignupScreen
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen.value }

        setContent {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                API.getUserData(viewModel.getAuthToken(this), {
                    viewModel.setUser(it)
                    viewModel.showSplashScreen.value = false
                    Log.d(TAG, "onCreate: fetched user")
                }) {
                    viewModel.showSplashScreen.value = false
                    Toast.makeText(this, "Please refresh session", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreate: fetch user error $it")
                }
            } else {
                viewModel.showSplashScreen.value = false
            }

            MotiClubsTheme(getColorScheme()) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    val systemUiController = rememberSystemUiController()
                    systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)

                    val localBackPressed = LocalOnBackPressedDispatcherOwner.current
                    val navController = rememberNavController()
                    var localClubModel = ClubModel("", "", "", "", listOf())

                    NavHost(
                        modifier = Modifier, navController = navController,
                        startDestination = if (user != null) AppNavigation.HOME else AppNavigation.LOGIN
                    ) {
                        // LOGIN
                        composable(AppNavigation.LOGIN) {
                            LoginScreen(appViewModel = viewModel, {
                                navController.navigate(AppNavigation.SIGN_UP)
                            }, {
                                navController.navigate(AppNavigation.HOME) {
                                    popUpTo(AppNavigation.LOGIN) {
                                        inclusive = true
                                    }
                                }
                            })
                        }

                        // SIGN UP
                        composable(AppNavigation.SIGN_UP) {
                            SignupScreen(appViewModel = viewModel, {
                                localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                            })
                        }

                        // HOME
                        composable(AppNavigation.HOME) {
                            HomeScreen(appViewModel = viewModel, onNavigateLogOut = {
                                navController.navigate(AppNavigation.LOGIN) {
                                    popUpTo(AppNavigation.HOME) {
                                        inclusive = true
                                    }
                                }
                            }, onNavigatePostItemClick = {
                                localClubModel = it
                                navController.navigate(AppNavigation.CLUB_PAGE)
                            })
                        }

                        // CLUB PAGE
                        composable(AppNavigation.CLUB_PAGE) { ClubScreen(clubModel = localClubModel) }
                    }
                }
            }
        }
    }
}
