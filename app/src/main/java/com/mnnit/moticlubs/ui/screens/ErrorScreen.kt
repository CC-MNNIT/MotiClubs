package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel

@Composable
fun ErrorScreen(viewModel: AppViewModel) {
    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()

        Surface(
            modifier = Modifier
                .fillMaxSize()
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
                            visible = viewModel.fetchingState,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        }
                        AnimatedVisibility(
                            visible = !viewModel.fetchingState,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
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
                                    onClick = { viewModel.getUser(FirebaseAuth.getInstance().currentUser) },
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
