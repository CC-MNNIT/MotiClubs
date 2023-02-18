package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mnnit.moticlubs.data.network.dto.ImageUrl
import com.mnnit.moticlubs.ui.components.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var imageUrl by mutableStateOf(
        savedStateHandle.get<ImageUrl>("image") ?: ImageUrl("")
    )
}

@Composable
fun ImageScreen(viewModel: ImageScreenViewModel = hiltViewModel()) {
    val colorScheme = getColorScheme()

    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()

        val scale = remember { mutableStateOf(1f) }
        val rotationState = remember { mutableStateOf(0f) }
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .clip(RectangleShape)
                .wrapContentSize()
                .background(colorScheme.background)
                .pointerInput(Unit) {
                    detectTransformGestures(panZoomLock = true) { _, pan, zoom, rotation ->
                        scale.value *= zoom
                        rotationState.value += rotation

                        if (scale.value > 1f) {
                            offsetX.value += pan.x
                            offsetY.value += pan.y
                        } else {
                            offsetX.value = 0f
                            offsetY.value = 0f
                            rotationState.value = 0f
                        }
                    }
                }
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = maxOf(1f, minOf(3f, scale.value)),
                        scaleY = maxOf(1f, minOf(3f, scale.value)),
                        rotationZ = rotationState.value,
                        translationX = offsetX.value,
                        translationY = offsetY.value
                    ),
                contentDescription = null,
                painter = LocalContext.current.getImageUrlPainter(url = viewModel.imageUrl.imageUrl)
            )
        }
    }
}
