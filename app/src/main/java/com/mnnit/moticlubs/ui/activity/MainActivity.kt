package com.mnnit.moticlubs.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.end
import com.mnnit.moticlubs.pxToDp
import com.mnnit.moticlubs.start
import com.mnnit.moticlubs.ui.screens.*
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
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen.value }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightNavigationBars = true

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            viewModel.paddingValues.value = PaddingValues(
                start = insets.left.pxToDp(this),
                top = insets.top.pxToDp(this),
                end = insets.right.pxToDp(this),
                bottom = insets.bottom.pxToDp(this)
            )
            WindowInsetsCompat.CONSUMED
        }

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

            val colorScheme = getColorScheme()
            MotiClubsTheme(colorScheme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .imePadding()
                ) {
                    val systemUiController = rememberSystemUiController()
                    val darkTheme = isSystemInDarkTheme()
                    DisposableEffect(systemUiController, darkTheme) {
                        systemUiController.setSystemBarsColor(color = colorScheme.background, darkIcons = !darkTheme)
                        onDispose { }
                    }

                    val localBackPressed = LocalOnBackPressedDispatcherOwner.current
                    val navController = rememberNavController()
                    var localClubModel = ClubModel("", "", "", "", listOf())

                    NavHost(
                        modifier = Modifier.padding(
                            start = viewModel.paddingValues.value.start(),
                            end = viewModel.paddingValues.value.end()
                        ),
                        navController = navController,
                        startDestination = if (user != null) AppNavigation.HOME else AppNavigation.LOGIN
                    ) {
                        // LOGIN
                        composable(AppNavigation.LOGIN) {
                            LoginScreen(appViewModel = viewModel, {
                                navController.navigate(AppNavigation.SIGN_UP)
                            }, {
                                navController.navigate(AppNavigation.HOME) {
                                    popUpTo(AppNavigation.LOGIN) { inclusive = true }
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
                            HomeScreen(
                                appViewModel = viewModel,
                                onNavigatePostItemClick = {
                                    localClubModel = it
                                    navController.navigate(AppNavigation.CLUB_PAGE)
                                },
                                onNavigateContactUs = { navController.navigate(AppNavigation.CONTACT_US) },
                                onNavigateProfile = { navController.navigate(AppNavigation.PROFILE) })
                        }

                        // CLUB PAGE
                        composable(AppNavigation.CLUB_PAGE) {
                            ClubScreen(_clubModel = localClubModel, appViewModel = viewModel)
                        }

                        // PROFILE
                        composable(AppNavigation.PROFILE) {
                            ProfileScreen(viewModel, onNavigationLogout = {
                                navController.navigate(AppNavigation.LOGIN) {
                                    popUpTo(AppNavigation.HOME) { inclusive = true }
                                }
                            })
                        }

                        // CONTACT US
                        composable(AppNavigation.CONTACT_US) {
                            ContactUsScreen()
                        }
                    }
                }
            }
        }
    }
}
