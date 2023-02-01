package com.mnnit.moticlubs.ui.screens

import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor() : ViewModel() {
    val initialClubModel = mutableStateOf(ClubModel("", "", "", "", listOf(), listOf()))
    val description = mutableStateOf("")
    val avatar_url = mutableStateOf("")

    val isLoading = mutableStateOf(false)

    val isEditButtonEnabled
        get() = !isLoading.value
                && ((initialClubModel.value.avatar != avatar_url.value) || (initialClubModel.value.description != description.value))

}

@Composable
fun ClubDetailsScreen(appViewModel: AppViewModel, viewModel: ClubDetailsScreenViewModel = hiltViewModel()) {
    viewModel.initialClubModel.value = appViewModel.clubModel.value
    viewModel.description.value = appViewModel.clubModel.value.description
    viewModel.avatar_url.value = appViewModel.clubModel.value.avatar

    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Scaffold(
                modifier = Modifier,
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Edit", fontSize = 15.sp, textAlign = TextAlign.Center) },
                        icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = "") },
                        onClick = { },
                        expanded = !viewModel.isEditButtonEnabled,
                        shape = RoundedCornerShape(24.dp),
                    )
                },
                floatingActionButtonPosition = FabPosition.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ClubProfilePic(
                        viewModel = viewModel,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    ClubInfo(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 36.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClubProfilePic(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
//            loading.value = true
//            updateProfilePicture(context, result.uriContent!!, appViewModel, loading)
            viewModel.avatar_url.value = result.uriContent!!.toString()
        } else {
            val exception = result.error
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(1, 1)
        imageCropLauncher.launch(cropOptions)
    }

    Row(modifier = modifier) {
        Image(
            painter = if (viewModel.avatar_url.value.isEmpty() || !viewModel.avatar_url.value.matches(
                    Patterns.WEB_URL.toRegex()
                )
            ) {
                rememberVectorPainter(image = Icons.Outlined.AccountCircle)
            } else {
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(viewModel.avatar_url.value)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(Constants.AVATAR)
                        .placeholder(R.drawable.outline_account_circle_24)
                        .build()
                )
            }, contentDescription = "",
            modifier = modifier
                .padding(start = 46.dp)
                .clip(CircleShape)
                .size(156.dp)
        )

        IconButton(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier
                .align(Alignment.Bottom)
                .border(1.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
        }
    }
}

@Composable
fun ClubInfo(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Text(viewModel.initialClubModel.value.name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.description.value,
            onValueChange = { viewModel.description.value = it },
            shape = RoundedCornerShape(24.dp),
            label = { Text(text = "Description") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
                disabledLeadingIconColor = contentColorFor(backgroundColor = colorScheme.background)
            )
        )
    }
}
