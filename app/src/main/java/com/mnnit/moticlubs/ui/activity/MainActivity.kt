package com.mnnit.moticlubs.ui.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.PostNotificationModel
import com.mnnit.moticlubs.api.PostParamType
import com.mnnit.moticlubs.ui.screens.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
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

            val postNotificationModel = remember { mutableStateOf(PostNotificationModel()) }
            val colorScheme = getColorScheme()
            MotiClubsTheme(colorScheme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .imePadding()
                ) {
                    SetNavBarsTheme()

                    val localBackPressed = LocalOnBackPressedDispatcherOwner.current
                    val navController = rememberNavController()

                    NavHost(
                        modifier = Modifier
                            .imePadding()
                            .systemBarsPadding(),
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
                            SignupScreen({
                                localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                            })
                        }

                        // HOME
                        composable(AppNavigation.HOME) {
                            HomeScreen(
                                appViewModel = viewModel,
                                onNavigatePostItemClick = {
                                    viewModel.clubModel.value = it
                                    navController.navigate(AppNavigation.CLUB_PAGE)
                                },
                                onNavigateContactUs = { navController.navigate(AppNavigation.CONTACT_US) },
                                onNavigateProfile = { navController.navigate(AppNavigation.PROFILE) })
                        }

                        // CLUB PAGE
                        composable(AppNavigation.CLUB_PAGE) {
                            ClubScreen(appViewModel = viewModel, onNavigateToPost = { post ->
                                navController.navigate("${AppNavigation.POST_PAGE}/${Uri.encode(Gson().toJson(post))}")
                            }, onNavigateToClubDetails = {
                                navController.navigate(AppNavigation.CLUB_DETAIL)
                            })
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

                        // CLUB POST
                        composable(
                            "${AppNavigation.POST_PAGE}/{post}",
                            arguments = listOf(navArgument("post") { type = PostParamType() }),
                            deepLinks = listOf(navDeepLink { uriPattern = "${Constants.POST_URL}/post={post}" })
                        ) {
                            postNotificationModel.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                it.arguments?.getParcelable(
                                    "post",
                                    PostNotificationModel::class.java
                                )
                            } else {
                                it.arguments?.getParcelable("post")
                            } ?: PostNotificationModel()
                            PostScreen(postNotificationModel)
                        }

                        // CLUB Details
                        composable(AppNavigation.CLUB_DETAIL) {
                            ClubDetailsScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
