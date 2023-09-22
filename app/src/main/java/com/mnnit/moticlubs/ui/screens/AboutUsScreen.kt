package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import com.mnnit.moticlubs.BuildConfig
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.DragHandle
import com.mnnit.moticlubs.ui.components.aboutus.AboutUsContactForm
import com.mnnit.moticlubs.ui.components.aboutus.ContributorDialog
import com.mnnit.moticlubs.ui.components.aboutus.DeveloperProfile
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.AboutUsViewModel

@Composable
fun AboutUsScreen(modifier: Modifier = Modifier, viewModel: AboutUsViewModel = hiltViewModel()) {
    val cc = "https://github.com/CC-MNNIT.png"
    val shank = "https://github.com/shank03.png"

    val scrollState = rememberScrollState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    MotiClubsTheme {
        SetTransparentSystemBars(setStatusBar = false, setNavBar = false)

        Surface(
            color = colorScheme.background,
            modifier = modifier
                .fillMaxWidth()
                .imePadding()
                .systemBarsPadding(),
        ) {
            BottomSheetScaffold(
                modifier = Modifier
                    .imePadding()
                    .systemBarsPadding(),
                scaffoldState = scaffoldState,
                sheetPeekHeight = 72.dp,
                sheetContainerColor = colorScheme.surfaceColorAtElevation(2.dp),
                sheetContent = {
                    AboutUsContactForm(modifier = Modifier)
                },
                sheetDragHandle = {
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            .imePadding()
                            .fillMaxWidth(),
                    ) {
                        DragHandle(modifier = Modifier.align(Alignment.CenterHorizontally))

                        Text(
                            text = "Contact Us",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                        )
                    }
                },
                content = {
                    if (viewModel.showContributorDialog) {
                        ContributorDialog(app = viewModel.contributorTagApp, viewModel)
                    }

                    Surface(
                        modifier = Modifier
                            .systemBarsPadding()
                            .fillMaxSize(),
                        color = colorScheme.background,
                    ) {
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
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                                colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                            .clip(CircleShape)
                                            .background(color = Color(0xFF323E4E)),
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.app_icon),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .size(108.dp)
                                                .padding(16.dp)
                                                .align(Alignment.CenterVertically),
                                        )
                                    }

                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = LocalContext.current.getString(R.string.app_name),
                                        textAlign = TextAlign.Center,
                                        fontSize = 24.sp,
                                    )
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                        textAlign = TextAlign.Center,
                                        fontSize = 15.sp,
                                    )

                                    DeveloperProfile(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(bottom = 8.dp),
                                        name = "Made with 💻\nBy CC Club - MNNIT",
                                        userModel = User().copy(avatar = cc),
                                        showIcons = false,
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                    ) {
                                        GithubLinkButton(
                                            viewModel,
                                            name = "App",
                                            url = "https://github.com/CC-MNNIT/MotiClubs",
                                        )
                                        Spacer(modifier = Modifier.padding(4.dp))
                                        GithubLinkButton(
                                            viewModel,
                                            name = "Backend",
                                            url = "https://github.com/CC-MNNIT/MotiClubs-Service",
                                        )
                                    }
                                }
                            }

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Maintainer(s)",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                            ) {
                                DeveloperProfile(
                                    userModel = User().copy(avatar = shank),
                                    linkedin = "https://linkedin.com/in/shank03",
                                    name = "Shashank Verma",
                                    stream = "CSE",
                                    year = "Final year",
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 102.dp, start = 16.dp, end = 16.dp),
                            ) {
                                GithubLinkButton(viewModel, name = "App\nContributors")
                                Spacer(modifier = Modifier.padding(8.dp))
                                GithubLinkButton(viewModel, name = "Backend\nContributors")
                            }
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.GithubLinkButton(
    viewModel: AboutUsViewModel,
    name: String,
    url: String = "",
) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
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
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.github),
                contentDescription = "",
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                text = name,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            )

            Spacer(modifier = Modifier.padding(2.dp))

            if (url.isTrimmedNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = "",
                )
            }
        }
    }
}
