package com.mnnit.moticlubs.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.ui.screens.ClubScreen
import com.mnnit.moticlubs.ui.screens.ClubScreenViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme

class ClubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clubModel = intent.getParcelableExtra<ClubModel>(Constants.CLUB)
        val clubScreenViewModel : ClubScreenViewModel by viewModels()
        setContent {
            MotiClubsTheme(getColorScheme()) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    val systemUiController = rememberSystemUiController()
                    systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)
                    ClubScreen(clubScreenViewModel = clubScreenViewModel , clubModel = clubModel!!, modifier = Modifier)
                }
            }
        }
    }

}