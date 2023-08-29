package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.ui.components.ConfirmationDialog
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.profilescreen.UpdateProfileIcon
import com.mnnit.moticlubs.ui.components.profilescreen.UserInfo
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AppViewModel
import com.mnnit.moticlubs.ui.viewmodel.HomeScreenViewModel

@Composable
fun ProfileScreen(
    appViewModel: AppViewModel,
    viewModel: HomeScreenViewModel,
    onNavigationLogout: () -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }

    MotiClubsTheme(getColorScheme()) {
        SetNavBarsTheme()
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .verticalScroll(scrollState)
                    .wrapContentHeight(Alignment.Top),
            ) {
                TopAppBar(
                    modifier = Modifier.padding(top = 16.dp),
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
                    url = viewModel.user.avatar,
                    size = 156.dp
                )
                UserInfo(viewModel = viewModel, modifier = Modifier.padding(top = 56.dp))

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
