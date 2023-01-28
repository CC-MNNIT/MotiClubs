package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.ui.theme.Blue
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@Composable
fun CircularProgressAnimated(){
    Box(modifier = Modifier.height(30.dp).width(30.dp)) {
        val progressValue = 0.75f
        val infiniteTransition = rememberInfiniteTransition()

        val progressAnimationValue by infiniteTransition.animateFloat(
            initialValue = 0.0f,
            targetValue = progressValue, animationSpec = infiniteRepeatable(animation = tween(900))
        )
        CircularProgressIndicator(progress = progressAnimationValue, color = Blue, modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun Preview1() {
    MotiClubsTheme(getColorScheme()) {
        CircularProgressAnimated()
    }
}
