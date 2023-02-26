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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.ui.components.BottomSheetForm
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun AboutUsScreen() {

    val cc = "https://github.com/CC-MNNIT.png"
    val shank = "https://github.com/shank03.png"
    val hitesh = "https://github.com/mitrukahitesh.png"
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
                BottomSheetForm()
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
                                github = cc,
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
                                github = shank,
                                linkedin = "https://linkedin.com/in/shank03",
                                name = "Shashank Verma",
                                stream = "CSE",
                                year = "Pre-final year"
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            DeveloperProfile(
                                github = hitesh,
                                linkedin = "https://linkedin.com/in/mitrukahitesh",
                                name = "Hitesh Mitruka",
                                stream = "CSE",
                                year = "Pre-final year"
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            DeveloperProfile(
                                github = amit,
                                linkedin = "https://www.linkedin.com/in/amit3210",
                                name = "Amit Kumar",
                                stream = "CSE",
                                year = "Pre-final year"
                            )
                        }
                    }
                }
            })
    }
}

@Composable
fun ColumnScope.DeveloperProfile(
    modifier: Modifier = Modifier,
    github: String = "", linkedin: String = "",
    name: String, stream: String = "", year: String = "",
    showIcons: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val colorScheme = getColorScheme()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfilePicture(modifier = Modifier.align(Alignment.CenterVertically), url = github, size = 56.dp)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 32.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = name,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                if (stream.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                        text = stream,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (year.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                        text = year,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
            ) {
                if (showIcons) {
                    if (github.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                focusManager.clearFocus(true)
                                uriHandler.openUri(github.replace(".png", ""))
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterHorizontally),
                                painter = painterResource(id = R.drawable.github),
                                contentDescription = ""
                            )
                        }
                    }

                    if (linkedin.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        IconButton(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                focusManager.clearFocus(true)
                                uriHandler.openUri(linkedin)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterHorizontally),
                                painter = painterResource(id = R.drawable.linkedin),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}
