package com.mnnit.moticlubs.ui.screens

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.ui.activity.AppScreenMode
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.activity.MainScreenMode
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

}

@Composable
fun MainScreen(
    context: Context,
    appViewModel: AppViewModel,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    MotiClubsTheme(
        if (isSystemInDarkTheme()) {
            dynamicDarkColorScheme(context)
        } else dynamicLightColorScheme(context)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)

            Column(
                modifier = Modifier
                    .padding(top = 120.dp, start = 16.dp, end = 16.dp)
                    .animateContentSize()
            ) {
                Text(text = "MotiClubs", fontSize = 32.sp)

                Spacer(modifier = Modifier.padding(16.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        FirebaseAuth.getInstance().signOut()

                        appViewModel.appScreenMode.value = AppScreenMode.LOGIN
                        appViewModel.mainScreenMode.value = MainScreenMode.INVALID
                    },
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                ) {
                    Text(text = "Logout", fontSize = 14.sp)
                }
            }
        }
    }
}