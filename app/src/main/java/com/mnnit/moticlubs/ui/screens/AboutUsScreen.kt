@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mnnit.moticlubs.R
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
                            ) {
                                ProfilePicture(
                                    url = cc,
                                    size = 108.dp,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 16.dp),
                                text = "Made with 💻\nBy CC Club - MNNIT",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Developers",
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
    github: String, linkedin: String, name: String,
    stream: String, year: String
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
            ProfilePicture(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                url = github,
                size = 56.dp
            )
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

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp),
                    text = stream,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp),
                    text = year,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
            ) {
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

@Composable
fun BottomSheetForm() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val colorScheme = getColorScheme()
    var name by remember { mutableStateOf("") }
    var postMsg by remember { mutableStateOf("") }

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .imePadding()
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(4.dp))
                    .background(contentColorFor(backgroundColor = colorScheme.background))
            ) {
                Text(text = "", modifier = Modifier.padding(12.dp))
            }

            Text(
                text = "Contact Us",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    value = name,
                    onValueChange = { name = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Name") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                        disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .weight(1f),
                    value = postMsg,
                    onValueChange = { postMsg = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Message") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                        disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                    )
                )

                AssistChip(
                    onClick = {
                        keyboardController?.hide()
                        Toast.makeText(context, "No Domain 🤨", Toast.LENGTH_SHORT).show()
                    },
                    label = {
                        Text(
                            text = "Send",
                            fontSize = 14.sp,
                            color = contentColorFor(
                                backgroundColor = if (postMsg.isNotEmpty() && name.isNotEmpty()) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                }
                            )
                        )
                    }, leadingIcon = {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.Send),
                            contentDescription = "",
                            tint = contentColorFor(
                                backgroundColor = if (postMsg.isNotEmpty() && name.isNotEmpty()) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.onSurface.copy(alpha = 0.38f)
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .imePadding()
                        .padding(bottom = 16.dp, top = 16.dp)
                        .align(Alignment.End),
                    shape = RoundedCornerShape(24.dp),
                    colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                    enabled = postMsg.isNotEmpty() && name.isNotEmpty()
                )
            }
        }
    }
}
