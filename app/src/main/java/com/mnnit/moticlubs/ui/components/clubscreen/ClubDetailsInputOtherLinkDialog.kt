package com.mnnit.moticlubs.ui.components.clubscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.util.OtherLinkComposeModel
import com.mnnit.moticlubs.domain.util.SocialLinkComposeModel
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.theme.getColorScheme
import kotlinx.coroutines.launch

@Composable
fun InputOtherLinkDialog(
    showDialog: MutableState<Boolean>,
    showColorPaletteDialog: MutableState<Boolean>,
    otherLinksLiveList: SnapshotStateList<OtherLinkComposeModel>,
    otherLinkIdx: MutableState<Int>,
    socialLinksLiveList: SnapshotStateList<SocialLinkComposeModel>,
    onClick: (list: List<UrlModel>) -> Unit
) {
    val colorScheme = getColorScheme()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    Dialog(
        onDismissRequest = { showDialog.value = false },
        DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(128.dp, 512.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Other Link Input",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                if (otherLinksLiveList.isEmpty()) {
                    otherLinksLiveList.add(OtherLinkComposeModel())
                }

                LazyColumn(
                    state = listState, modifier = Modifier.weight(1f, false)
                ) {
                    items(otherLinksLiveList.size) { idx ->
                        OtherLinkItem(
                            modifier = Modifier.animateItemPlacement(),
                            idx,
                            otherLinksLiveList,
                            otherLinkIdx,
                            showColorPaletteDialog
                        ) { id -> scope.launch { listState.animateScrollToItem(id) } }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                ) {
                    Spacer(
                        modifier = Modifier
                            .border(1.dp, color = colorScheme.primary)
                            .fillMaxWidth()
                    )
                    IconButton(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.End),
                        onClick = {
                            otherLinksLiveList.add(OtherLinkComposeModel())
                            scope.launch { listState.animateScrollToItem(otherLinksLiveList.size - 1) }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.primary,
                            contentColor = contentColorFor(backgroundColor = colorScheme.primary)
                        )
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    }

                    Button(
                        onClick = {
                            val list = socialLinksLiveList
                                .filter { it.validUrl() }.map { it.mapToUrlModel() }
                                .toMutableList()
                            val others = otherLinksLiveList
                                .filter { it.validUrl() && it.getName().isTrimmedNotEmpty() }.map { it.mapToUrlModel() }
                            list.addAll(others)
                            onClick(list)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Save Link", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
