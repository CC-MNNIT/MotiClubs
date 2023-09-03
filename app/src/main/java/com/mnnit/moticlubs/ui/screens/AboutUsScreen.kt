@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.components.aboutus.AboutUsContactForm
import com.mnnit.moticlubs.ui.components.aboutus.DeveloperProfile
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun AboutUsScreen() {

    val cc = "https://github.com/CC-MNNIT.png"
    val shank = "https://github.com/shank03.png"
    val amit = "https://github.com/hackeramitkumar.png"

    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val systemUiController = rememberSystemUiController()

    MotiClubsTheme(colorScheme) {
        systemUiController.setNavigationBarColor(
            color = colorScheme.surfaceColorAtElevation(2.dp),
            darkIcons = !isSystemInDarkTheme()
        )

        systemUiController.setStatusBarColor(
            color = if (scaffoldState.bottomSheetState.isExpanded) colorScheme.surfaceColorAtElevation(2.dp) else colorScheme.background,
            darkIcons = !isSystemInDarkTheme()
        )

        BottomSheetScaffold(modifier = Modifier.imePadding(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 72.dp,
            sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp),
            sheetContent = {
                AboutUsContactForm()
            }, content = {
                Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .wrapContentHeight(Alignment.Top),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp)
                                    .clip(CircleShape)
                                    .background(color = Color(0xFF323E4E))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_icon), contentDescription = "",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(108.dp)
                                        .padding(16.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                text = LocalContext.current.getString(R.string.app_name),
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp
                            )

                            DeveloperProfile(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 16.dp),
                                name = "Made with 💻\nBy CC Club - MNNIT",
                                userModel = User().copy(avatar = cc),
                                showIcons = false
                            )
                        }

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Maintainers",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 102.dp)
                        ) {
                            DeveloperProfile(
                                userModel = User().copy(avatar = shank),
                                linkedin = "https://linkedin.com/in/shank03",
                                name = "Shashank Verma",
                                stream = "CSE",
                                year = "Final year"
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            DeveloperProfile(
                                userModel = User().copy(avatar = amit),
                                linkedin = "https://www.linkedin.com/in/amit3210",
                                name = "Amit Kumar",
                                stream = "CSE",
                                year = "Final year"
                            )
                        }
                    }
                }
            })
    }
}
