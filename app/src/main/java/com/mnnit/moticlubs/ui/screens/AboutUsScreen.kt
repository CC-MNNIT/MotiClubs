package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.aboutus.AboutUsContactForm
import com.mnnit.moticlubs.ui.components.aboutus.ContributorDialog
import com.mnnit.moticlubs.ui.components.aboutus.DeveloperProfile
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AboutUsViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutUsScreen(viewModel: AboutUsViewModel = hiltViewModel()) {

    val cc = "https://github.com/CC-MNNIT.png"
    val shank = "https://github.com/shank03.png"

    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    val scaffoldState = rememberBottomSheetScaffoldState()

    MotiClubsTheme(colorScheme) {
        SetTransparentSystemBars(setStatusBar = false, setNavBar = false)

        BottomSheetScaffold(
            modifier = Modifier
                .imePadding()
                .systemBarsPadding(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 72.dp,
            sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp),
            sheetContent = {
                AboutUsContactForm()
            }, content = {
                if (viewModel.showContributorDialog) {
                    ContributorDialog(app = viewModel.contributorTagApp, viewModel)
                }

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
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
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
                                        .padding(bottom = 8.dp),
                                    name = "Made with 💻\nBy CC Club - MNNIT",
                                    userModel = User().copy(avatar = cc),
                                    showIcons = false
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    GithubLinkButton(
                                        viewModel,
                                        name = "App",
                                        url = "https://github.com/CC-MNNIT/MotiClubs"
                                    )
                                    GithubLinkButton(
                                        viewModel,
                                        name = "Backend",
                                        url = "https://github.com/CC-MNNIT/MotiClubs-Service"
                                    )
                                }
                            }
                        }

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Maintainer(s)",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                        ) {
                            DeveloperProfile(
                                userModel = User().copy(avatar = shank),
                                linkedin = "https://linkedin.com/in/shank03",
                                name = "Shashank Verma",
                                stream = "CSE",
                                year = "Final year"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 102.dp)
                        ) {
                            GithubLinkButton(viewModel, name = "App\nContributors")
                            GithubLinkButton(viewModel, name = "Backend\nContributors")
                        }
                    }
                }
            })
    }
}

@Composable
private fun RowScope.GithubLinkButton(
    viewModel: AboutUsViewModel,
    name: String,
    url: String = "",
) {
    val uriHandler = LocalUriHandler.current
    val colorScheme = getColorScheme()

    Card(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)
            .padding(horizontal = 16.dp),
        onClick = {
            if (url.isEmpty()) {
                viewModel.contributorTagApp = name.startsWith("App")
                viewModel.getContributors()
                viewModel.showContributorDialog = true
            } else {
                uriHandler.openUri(url)
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.github),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = name,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            if (url.isTrimmedNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = ""
                )
            }
        }
    }
}
