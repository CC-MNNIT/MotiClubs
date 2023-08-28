package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.ui.components.PullDownProgressIndicator
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.ChannelDetailScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChannelDetailScreen(
    onBackPressed: () -> Unit,
    viewModel: ChannelDetailScreenViewModel = hiltViewModel(),
) {
    val colorScheme = getColorScheme()
    val scrollState = rememberScrollState()

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetching,
        onRefresh = viewModel::refreshAll
    )
    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme(2.dp, false)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .pullRefresh(state = refreshState)
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(scrollState)
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    PullDownProgressIndicator(
                        modifier = Modifier.background(colorScheme.surfaceColorAtElevation(2.dp)),
                        visible = viewModel.isFetching,
                        refreshState = refreshState
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .pullRefresh(state = refreshState)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .size(42.dp),
                                onClick = onBackPressed
                            ) {
                                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                            }

                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = viewModel.channelModel.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                modifier = Modifier.padding(top = 0.dp),
                                text = "${
                                    if (viewModel.channelModel.private == 1) {
                                        viewModel.memberList.size
                                    } else "All"
                                } members",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//private fun ClubProfilePic(
//    onNavigateBackPressed: () -> Unit,
//    viewModel: ClubDetailsScreenViewModel,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//
//    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            viewModel.progressMsg = "Uploading ..."
//            viewModel.showProgressDialog.value = true
//            updateClubProfilePicture(context, result.uriContent!!, viewModel, viewModel.showProgressDialog)
//        } else {
//            val exception = result.error
//            Toast.makeText(context, "Error ${exception?.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
//        cropOptions.setAspectRatio(1, 1)
//        imageCropLauncher.launch(cropOptions)
//    }
//
//    Column(modifier = modifier) {
//        Row(modifier = Modifier.fillMaxWidth()) {
//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .size(42.dp),
//                onClick = { onNavigateBackPressed() }
//            ) {
//                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            if (viewModel.isAdmin) {
//                IconButton(
//                    onClick = { launcher.launch("image/*") },
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .size(42.dp),
//                ) {
//                    Icon(imageVector = Icons.Rounded.AddAPhoto, contentDescription = "")
//                }
//            }
//        }
//
//        ProfilePicture(
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            url = viewModel.clubModel.avatar,
//            size = 156.dp
//        )
//    }
//}
