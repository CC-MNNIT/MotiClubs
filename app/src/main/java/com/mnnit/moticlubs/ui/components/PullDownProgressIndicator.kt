package com.mnnit.moticlubs.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullDownProgressIndicator(
    modifier: Modifier = Modifier,
    visible: Boolean,
    refreshState: PullRefreshState
) {
    AnimatedVisibility(
        visible = visible || refreshState.progress > 0.5f,
        modifier = modifier.fillMaxWidth()
    ) {
        if (refreshState.progress > 0.5f) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                strokeCap = StrokeCap.Round,
                progress = 0f
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }
}
