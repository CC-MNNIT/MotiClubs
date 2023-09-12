package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mnnit.moticlubs.domain.util.getStringArg
import com.mnnit.moticlubs.domain.util.zoomableContentLocation
import com.mnnit.moticlubs.ui.components.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import javax.inject.Inject

@HiltViewModel
class ImageScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var imageUrl by mutableStateOf(savedStateHandle.getStringArg("image"))

}

@Composable
fun ImageScreen(
    onBackPressed: () -> Unit,
    viewModel: ImageScreenViewModel = hiltViewModel()
) {
    val colorScheme = getColorScheme()

    val painter = LocalContext.current.getImageUrlPainter(url = viewModel.imageUrl)
    val zoomState = rememberZoomableState()

    MotiClubsTheme(colorScheme) {
        SetTransparentSystemBars()

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState),
                contentDescription = null,
                alignment = Alignment.Center,
                painter = painter
            )

            LaunchedEffect(painter.intrinsicSize) {
                zoomState.setContentLocation(painter.zoomableContentLocation())
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .systemBarsPadding()
                    .padding(16.dp)
                    .size(42.dp),
                onClick = onBackPressed,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colorScheme.background)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
            }
        }
    }
}
