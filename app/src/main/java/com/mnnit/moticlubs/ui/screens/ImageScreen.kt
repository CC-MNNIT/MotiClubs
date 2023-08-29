package com.mnnit.moticlubs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mnnit.moticlubs.domain.util.getStringArg
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

    var imageUrl by mutableStateOf(savedStateHandle.getStringArg("image"))

}

@Composable
fun ImageScreen(viewModel: ImageScreenViewModel = hiltViewModel()) {
    val colorScheme = getColorScheme()

    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme()

        val scale = remember { mutableFloatStateOf(1f) }
        val rotationState = remember { mutableFloatStateOf(0f) }
        val offsetX = remember { mutableFloatStateOf(0f) }
        val offsetY = remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .clip(RectangleShape)
                .wrapContentSize()
                .background(colorScheme.background)
                .pointerInput(Unit) {
                    detectTransformGestures(panZoomLock = true) { _, pan, zoom, rotation ->
                        scale.floatValue *= zoom
                        rotationState.floatValue += rotation

                        if (scale.floatValue > 1f) {
                            offsetX.floatValue += pan.x
                            offsetY.floatValue += pan.y
                        } else {
                            offsetX.floatValue = 0f
                            offsetY.floatValue = 0f
                            rotationState.floatValue = 0f
                        }
                    }
                }
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = maxOf(1f, minOf(3f, scale.floatValue)),
                        scaleY = maxOf(1f, minOf(3f, scale.floatValue)),
                        rotationZ = rotationState.floatValue,
                        translationX = offsetX.floatValue,
                        translationY = offsetY.floatValue
                    ),
                contentDescription = null,
                painter = LocalContext.current.getImageUrlPainter(url = viewModel.imageUrl)
            )
        }
    }
}
