package com.mnnit.moticlubs.ui.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.api.PostNotificationModel
import com.mnnit.moticlubs.api.PostParamType
import com.mnnit.moticlubs.ui.screens.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen.value }
        super.onCreate(savedInstanceState)

        setContent {
            val user = FirebaseAuth.getInstance().currentUser
            viewModel.fetchUser(user, this)

            AnimatedVisibility(visible = viewModel.showErrorScreen.value) {
                ErrorScreen()
            }
            AnimatedVisibility(visible = !viewModel.showErrorScreen.value) {
                MainScreen(user = user)
            }
        }
    }

    @Composable
    fun MainScreen(user: FirebaseUser?) {
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

    @Composable
    fun ErrorScreen() {
        val context = LocalContext.current
        val colorScheme = getColorScheme()
        MotiClubsTheme(colorScheme) {
            SetNavBarsTheme()

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
                    .imePadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                        ) {
                            AnimatedVisibility(
                                visible = viewModel.fetchingState.value,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(36.dp))
                            }
                            AnimatedVisibility(
                                visible = !viewModel.fetchingState.value,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .align(Alignment.CenterHorizontally),
                                        imageVector = Icons.Outlined.ErrorOutline, contentDescription = ""
                                    )
                                    Text(
                                        "Unable to connect to server", fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 24.dp)
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.fetchUser(
                                                FirebaseAuth.getInstance().currentUser,
                                                context
                                            )
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 16.dp, bottom = 16.dp)
                                    ) {
                                        Icon(
                                            painter = rememberVectorPainter(image = Icons.Rounded.Refresh),
                                            contentDescription = ""
                                        )
                                        Text(
                                            text = "Refresh",
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
