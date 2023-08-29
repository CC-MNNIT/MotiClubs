package com.mnnit.moticlubs.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.data.network.dto.ImageUrl
import com.mnnit.moticlubs.domain.util.*
import com.mnnit.moticlubs.ui.screens.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    private val requestPermission: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            openApp()
        } else {
            Toast.makeText(this, "Please enable notification permission for this app", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { viewModel.showSplashScreen }
        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result -> AuthHandler.onResult(result) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            validateNotificationPermission()
        } else {
            openApp()
        }
    }

    private fun openApp() {
        val user = FirebaseAuth.getInstance().currentUser
        viewModel.getUser(user)

        setContent {
            AnimatedVisibility(visible = viewModel.showErrorScreen) {
                ErrorScreen(viewModel)
            }
            AnimatedVisibility(visible = !viewModel.showErrorScreen) {
                MainScreen(user = user)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun validateNotificationPermission() {
        when {
            (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            )) == PackageManager.PERMISSION_GRANTED -> openApp()

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Toast.makeText(this, "Please enable notification permission for this app", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            else -> requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    fun MainScreen(user: FirebaseUser?) {
        val colorScheme = getColorScheme()
        MotiClubsTheme(colorScheme) {
            SetNavBarsTheme()

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                val localBackPressed = LocalOnBackPressedDispatcherOwner.current
                val navController = rememberNavController()

                NavHost(
                    modifier = Modifier
                        .imePadding()
                        .systemBarsPadding(),
                    navController = navController,
                    startDestination = if (user != null) AppNavigation.HOME else AppNavigation.LOGIN,
                ) {
                    // LOGIN
                    composable(AppNavigation.LOGIN) {
                        LoginScreen(
                            oneTapClient,
                            signInRequest,
                            googleSignInLauncher,
                            viewModel,
                            onNavigateToMain = {
                                navController.navigate(AppNavigation.HOME) {
                                    popUpTo(AppNavigation.LOGIN) { inclusive = true }
                                }
                                navController.graph.setStartDestination(AppNavigation.HOME)
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
                            onNavigateChannelClick = { channel, club ->
                                navController.navigate(
                                    "${AppNavigation.CLUB_PAGE}?" +
                                            "${NavigationArgs.CHANNEL_ARG}=${Uri.encode(Gson().toJson(channel))}&" +
                                            "${NavigationArgs.CLUB_ARG}=${Uri.encode(Gson().toJson(club))}&" +
                                            "${NavigationArgs.USER_ARG}=${Uri.encode(Gson().toJson(viewModel.user))}"
                                )
                            },
                            onNavigateContactUs = { navController.navigate(AppNavigation.ABOUT_US) },
                            onNavigateProfile = { navController.navigate(AppNavigation.PROFILE) },
                            onNavigateToClubDetails = { club, user ->
                                navController.navigate(
                                    "${AppNavigation.CLUB_DETAIL}?" +
                                            "${NavigationArgs.CLUB_ARG}=${Uri.encode(Gson().toJson(club))}&" +
                                            "${NavigationArgs.USER_ARG}=${Uri.encode(Gson().toJson(user))}"
                                )
                            }
                        )
                    }

                    // CLUB PAGE
                    composable(
                        "${AppNavigation.CLUB_PAGE}?" +
                                "${NavigationArgs.CHANNEL_ARG}={${NavigationArgs.CHANNEL_ARG}}&" +
                                "${NavigationArgs.CLUB_ARG}={${NavigationArgs.CLUB_ARG}}&" +
                                "${NavigationArgs.USER_ARG}={${NavigationArgs.USER_ARG}}",
                        arguments = listOf(
                            navArgument(NavigationArgs.CHANNEL_ARG) { type = ChannelParamType() },
                            navArgument(NavigationArgs.CLUB_ARG) { type = ClubParamType() },
                            navArgument(NavigationArgs.USER_ARG) { type = UserParamType() }
                        )
                    ) {
                        ClubScreen(onNavigateToPost = { post ->
                            navController.navigate("${AppNavigation.POST_PAGE}/${Uri.encode(Gson().toJson(post))}")
                        }, onNavigateToClubDetails = { club, user ->
                            navController.navigate(
                                "${AppNavigation.CLUB_DETAIL}?" +
                                        "${NavigationArgs.CLUB_ARG}=${Uri.encode(Gson().toJson(club))}&" +
                                        "${NavigationArgs.USER_ARG}=${Uri.encode(Gson().toJson(user))}"
                            )
                        }, onNavigateToImageScreen = {
                            navController.navigate("${AppNavigation.IMAGE_PAGE}/${Uri.encode(Gson().toJson(ImageUrl(it)))}")
                        }, onNavigateToChannelDetails = { club, channel ->
                            navController.navigate(
                                "${AppNavigation.CHANNEL_DETAIL}?" +
                                        "${NavigationArgs.CHANNEL_ARG}=${Uri.encode(Gson().toJson(channel))}&" +
                                        "${NavigationArgs.CLUB_ARG}=${Uri.encode(Gson().toJson(club))}"
                            )
                        }, onBackPressed = {
                            localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                        })
                    }

                    composable(
                        "${AppNavigation.CHANNEL_DETAIL}?" +
                                "${NavigationArgs.CHANNEL_ARG}={${NavigationArgs.CHANNEL_ARG}}&" +
                                "${NavigationArgs.CLUB_ARG}={${NavigationArgs.CLUB_ARG}}",
                        arguments = listOf(
                            navArgument(NavigationArgs.CHANNEL_ARG) { type = ChannelParamType() },
                            navArgument(NavigationArgs.CLUB_ARG) { type = ClubParamType() }
                        )
                    ) {
                        ChannelDetailScreen(
                            onBackPressed = {
                                localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                            }
                        )
                    }

                    // PROFILE
                    composable(AppNavigation.PROFILE) {
                        ProfileScreen(viewModel, onNavigationLogout = {
                            navController.navigate(AppNavigation.LOGIN) {
                                popUpTo(AppNavigation.HOME) { inclusive = true }
                            }
                        }, onBackPressed = {
                            localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                        })
                    }

                    // ABOUT US
                    composable(AppNavigation.ABOUT_US) { AboutUsScreen() }

                    // CLUB POST
                    composable(
                        "${AppNavigation.POST_PAGE}/{${NavigationArgs.POST_ARG}}",
                        arguments = listOf(navArgument(NavigationArgs.POST_ARG) { type = PostParamType() }),
                        deepLinks = listOf(navDeepLink {
                            uriPattern =
                                "${Constants.APP_SCHEME_URL}/${NavigationArgs.POST_ARG}={${NavigationArgs.POST_ARG}}"
                        })
                    ) {
                        PostScreen(onNavigateImageClick = {
                            navController.navigate("${AppNavigation.IMAGE_PAGE}/${Uri.encode(Gson().toJson(ImageUrl(it)))}")
                        }, onNavigateBackPressed = {
                            localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                        })
                    }

                    // CLUB Details
                    composable(
                        "${AppNavigation.CLUB_DETAIL}?" +
                                "${NavigationArgs.CLUB_ARG}={${NavigationArgs.CLUB_ARG}}&" +
                                "${NavigationArgs.USER_ARG}={${NavigationArgs.USER_ARG}}",
                        arguments = listOf(
                            navArgument(NavigationArgs.CLUB_ARG) { type = ClubParamType() },
                            navArgument(NavigationArgs.USER_ARG) { type = UserParamType() }
                        )
                    ) {
                        ClubDetailsScreen(onNavigateBackPressed = {
                            localBackPressed?.onBackPressedDispatcher?.onBackPressed()
                        })
                    }

                    // CLUB POST
                    composable(
                        "${AppNavigation.IMAGE_PAGE}/{image}",
                        arguments = listOf(navArgument("image") { type = ImageUrlParamType() })
                    ) {
                        ImageScreen()
                    }
                }
            }
        }
    }

//    @Composable
//    inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
//        val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
//        val parentEntry = remember(this) {
//            navController.getBackStackEntry(navGraphRoute)
//        }
//        return hiltViewModel(parentEntry)
//    }
}
