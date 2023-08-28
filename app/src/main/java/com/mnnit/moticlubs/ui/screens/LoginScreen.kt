package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.util.AuthHandler
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
    appViewModel: AppViewModel,
    onNavigateToMain: () -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val colorScheme = getColorScheme()
    val systemUiController = rememberSystemUiController()
    val bgColor = colorResource(id = R.color.splashColor)

    val context = LocalContext.current
    MotiClubsTheme(colorScheme) {
        LaunchedEffect(systemUiController) {
            systemUiController.setStatusBarColor(
                color = bgColor,
                darkIcons = false
            )
            systemUiController.setNavigationBarColor(
                color = bgColor,
                darkIcons = false
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = bgColor
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 56.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(176.dp),
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.padding(16.dp))

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.padding(24.dp))

                    AnimatedVisibility(
                        visible = !viewModel.isLoading.value,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(Color.White),
                            border = BorderStroke(1.dp, Color(0xFFEAEAEA)),
                            onClick = {
                                keyboardController?.hide()
                                viewModel.isLoading.value = true

                                AuthHandler.oneTapGoogleSignIn(
                                    oneTapClient,
                                    signInRequest,
                                    launcher,
                                    onSuccess = { credential, preUser ->
                                        viewModel.login(
                                            context,
                                            credential,
                                            preUser,
                                            appViewModel,
                                            onNavigateToMain
                                        )
                                    },
                                    onFailure = {
                                        viewModel.isLoading.value = false
                                        Toast.makeText(context, "Error: ${it.localizedMessage}", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                )
                            },
                            enabled = !viewModel.isLoading.value
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Image(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .size(24.dp),
                                    painter = painterResource(id = R.drawable.google_button),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                Text(
                                    text = "Sign in with Google",
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically),
                                    color = Color(0xFF242424)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = viewModel.isLoading.value,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}
