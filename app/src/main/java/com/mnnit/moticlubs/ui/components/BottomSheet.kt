package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.ui.theme.colorScheme

val SheetState.isExpanded: Boolean get() = currentValue == SheetValue.Expanded
val SheetState.isPartiallyExpanded: Boolean get() = currentValue == SheetValue.PartiallyExpanded
val SheetState.isHidden: Boolean get() = currentValue == SheetValue.Hidden

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .padding(vertical = 8.dp),
        color = contentColorFor(backgroundColor = colorScheme.background),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Box(Modifier.size(width = 32.dp, height = 4.dp))
    }
}
