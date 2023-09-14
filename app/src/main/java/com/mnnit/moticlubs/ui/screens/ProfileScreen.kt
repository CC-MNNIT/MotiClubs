package com.mnnit.moticlubs.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.profilescreen.UpdateProfileIcon
import com.mnnit.moticlubs.ui.components.profilescreen.UserInfo
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel
import java.nio.charset.StandardCharsets
import java.util.Base64

@Composable
fun ProfileScreen(
    appViewModel: AppViewModel,
    viewModel: HomeScreenViewModel,
    onNavigationLogout: () -> Unit,
    onBackPressed: () -> Unit
) {
    val colorScheme = getColorScheme()
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    MotiClubsTheme(getColorScheme()) {
        SetTransparentSystemBars()
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .verticalScroll(scrollState)
                    .wrapContentHeight(Alignment.Top),
            ) {
                TopAppBar(
                    modifier = Modifier,
                    title = {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                    actions = {
                        UpdateProfileIcon(
                            appViewModel = viewModel,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 16.dp),
                            loading
                        )
                    }
                )

                ProfilePicture(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    userModel = viewModel.userModel,
                    size = 156.dp
                )
                UserInfo(
                    userModel = viewModel.userModel,
                    modifier = Modifier.padding(top = 56.dp),
                    leadingIcon = {
                        IconButton(
                            modifier = Modifier
                                .size(42.dp)
                                .padding(start = 1.dp),
                            onClick = {
                                if (viewModel.editingEnabled.value) {
                                    viewModel.updateContactInfo()
                                    viewModel.editingEnabled.value = false
                                } else {
                                    viewModel.editingEnabled.value = true
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(colorScheme.primary),
                        ) {
                            Icon(
                                imageVector = if (viewModel.editingEnabled.value) {
                                    Icons.Rounded.Save
                                } else {
                                    Icons.Rounded.Edit
                                },
                                contentDescription = ""
                            )
                        }
                    },
                    contactText = viewModel.eventContact,
                    enabled = viewModel.editingEnabled,
                )

                Card(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    onClick = {
                        clipboardManager.setText(
                            AnnotatedString(
                                Base64.getEncoder()
                                    .encode(viewModel.userModel.userId.toString().toByteArray())
                                    .toString(StandardCharsets.UTF_8)
                            )
                        )
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Copy unique ID", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.padding(8.dp))
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "")
                    }
                }

                Button(
                    onClick = {
                        showDialog.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Icon(painter = rememberVectorPainter(image = Icons.Rounded.Logout), contentDescription = "")
                    Text(text = "Logout", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
            if (loading.value) {
                ProgressDialog(progressMsg = "Uploading ...")
            }
            if (showDialog.value) {
                ConfirmationDialog(
                    showDialog = showDialog,
                    message = "Are you sure you want to logout ?",
                    positiveBtnText = "Logout",
                    imageVector = Icons.Rounded.Logout,
                    onPositive = {
                        appViewModel.logoutUser()
                        onNavigationLogout()
                    }
                )
            }
        }
    }
}
