package com.mnnit.moticlubs.ui.components.aboutus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.colorScheme

@Composable
fun ColumnScope.DeveloperProfile(
    userModel: User,
    name: String,
    modifier: Modifier = Modifier,
    linkedin: String = "",
    stream: String = "",
    year: String = "",
    showIcons: Boolean = true,
    icons: (@Composable () -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.CenterVertically),
                userModel = userModel,
                size = 56.dp,
                onClick = {},
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 32.dp),
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = name,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                if (stream.isTrimmedNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                        text = stream,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                if (year.isTrimmedNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                        text = year,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight(),
            ) {
                if (showIcons) {
                    if (icons != null) {
                        icons()
                    } else {
                        if (userModel.avatar.isTrimmedNotEmpty()) {
                            IconButton(
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    focusManager.clearFocus(true)
                                    uriHandler.openUri(userModel.avatar.replace(".png", ""))
                                },
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.CenterHorizontally),
                                    painter = painterResource(id = R.drawable.github),
                                    contentDescription = "",
                                )
                            }
                        }

                        if (linkedin.isTrimmedNotEmpty()) {
                            Spacer(modifier = Modifier.padding(8.dp))
                            IconButton(
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    focusManager.clearFocus(true)
                                    uriHandler.openUri(linkedin)
                                },
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.CenterHorizontally),
                                    painter = painterResource(id = R.drawable.linkedin),
                                    contentDescription = "",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
