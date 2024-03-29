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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mnnit.moticlubs.domain.util.getStringArg
import com.mnnit.moticlubs.domain.util.getValue
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.domain.util.setValue
import com.mnnit.moticlubs.domain.util.zoomableContentLocation
import com.mnnit.moticlubs.ui.components.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.colorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import javax.inject.Inject

@HiltViewModel
class ImageScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var imageUrl by publishedStateOf(savedStateHandle.getStringArg("image"))
}

@Composable
fun ImageScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageScreenViewModel = hiltViewModel(),
) {
    val painter = LocalContext.current.getImageUrlPainter(url = viewModel.imageUrl)
    val zoomState = rememberZoomableState()

    MotiClubsTheme {
        SetTransparentSystemBars()

        Box(modifier = modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState),
                contentDescription = null,
                alignment = Alignment.Center,
                painter = painter,
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
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colorScheme.background),
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
            }
        }
    }
}
