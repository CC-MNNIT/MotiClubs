package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.PostResponse
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.Blue
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClubScreenViewModel @Inject constructor() : ViewModel() {

    val postsList = mutableStateListOf<PostResponse>()

    fun setPostsList(list: List<PostResponse>) {
        postsList.clear()
        list.forEach { postsList.add(it) }
    }
}

private val headerHeight = 250.dp
private val toolbarHeight = 56.dp

private val paddingMedium = 16.dp

private val titlePaddingStart = 16.dp
private val titlePaddingEnd = 72.dp

private const val titleFontScaleStart = 1f
private const val titleFontScaleEnd = 0.66f

@Composable
fun ClubScreen(
    appViewModel: AppViewModel,
    viewModel: ClubScreenViewModel = hiltViewModel(),
    clubId: String,
    clubName: String,
    clubDescription: String
) {
    API.getClubPosts(appViewModel.getAuthToken(LocalContext.current), clubID = clubId, {
        viewModel.setPostsList(it)
    }) {}

    MotiClubsTheme(getColorScheme()) {
        androidx.compose.material3.Surface(modifier = Modifier.fillMaxHeight()) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                parrallexEffectClubPage(clubName, clubId, clubDescription, viewModel, appViewModel)
            }
        }
    }
}

@Composable
fun parrallexEffectClubPage(
    clubName: String,
    clubId: String,
    clubDescription: String,
    viewModel: ClubScreenViewModel,
    appViewModel: AppViewModel
) {
    val scroll: ScrollState = rememberScrollState(0)
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(scroll, headerHeightPx, clubName, clubDescription)
        Body(scroll, viewModel, appViewModel)
        Toolbar(scroll, headerHeightPx, toolbarHeightPx)
        Title(scroll, headerHeightPx, toolbarHeightPx)
    }
}

@Composable
private fun Header(
    scroll: ScrollState,
    headerHeightPx: Float,
    clubName: String,
    clubDescription: String
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight)
        .graphicsLayer {
            translationY = -scroll.value.toFloat() / 2f // Parallax effect
            alpha = (-1f / headerHeightPx) * scroll.value + 1
        }
        .background(color = Blue)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA000000)),
                        startY = 3 * headerHeightPx / 4 // Gradient applied to wrap the title only
                    )
                )
        ) {
            androidx.compose.material3.Text(text = clubName, fontSize = 16.sp)
            androidx.compose.material3.Text(
                text = clubDescription,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

@Composable
private fun Body(scroll: ScrollState, viewModel: ClubScreenViewModel, appViewModel: AppViewModel) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxHeight()
    ) {
        items(viewModel.postsList.size) { idx ->
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), onClick = { /*TODO*/ },
                shape = RoundedCornerShape(15.dp), elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(appViewModel.avatar.value)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .diskCacheKey(Constants.AVATAR)
                                .placeholder(R.drawable.outline_account_circle_24)
                                .build()
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth(0.9f)
                    ) {
                        androidx.compose.material3.Text(text = "Amit Kumar", fontSize = 16.sp)
                        androidx.compose.material3.Text(
                            text = viewModel.postsList[idx].time.toString(),
                            fontSize = 12.sp
                        )
                        androidx.compose.material3.Text(
                            text = viewModel.postsList[idx].message,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

                    androidx.compose.material3.BadgedBox(badge = {
                        androidx.compose.material3.Badge { androidx.compose.material3.Text(text = "10") }
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {}
                }
            }
        }
    }
}

@Composable
private fun Toolbar(scroll: ScrollState, headerHeightPx: Float, toolbarHeightPx: Float) {
    val toolbarBottom = headerHeightPx - toolbarHeightPx
    val showToolbar by remember {
        derivedStateOf {
            scroll.value >= toolbarBottom
        }
    }

    AnimatedVisibility(
        visible = showToolbar,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        TopAppBar(
            modifier = Modifier.background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xff026586), Color(0xff032C45))
                )
            ),
            navigationIcon = {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipToBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            },
            title = {},
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        )
    }
}

@Composable
private fun Title(
    scroll: ScrollState,
    headerHeightPx: Float,
    toolbarHeightPx: Float
) {
    var titleHeightPx by remember { mutableStateOf(0f) }
    var titleWidthPx by remember { mutableStateOf(0f) }

    Text(
        text = "Club Posts",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .graphicsLayer {
                val collapseRange: Float = (headerHeightPx - toolbarHeightPx)
                val collapseFraction: Float = (scroll.value / collapseRange).coerceIn(0f, 1f)

                val scaleXY = lerp(
                    titleFontScaleStart.dp,
                    titleFontScaleEnd.dp,
                    collapseFraction
                )

                val titleExtraStartPadding = titleWidthPx.toDp() * (1 - scaleXY.value) / 2f

                val titleYFirstInterpolatedPoint = lerp(
                    headerHeight - titleHeightPx.toDp() - paddingMedium,
                    headerHeight / 2,
                    collapseFraction
                )

                val titleXFirstInterpolatedPoint = lerp(
                    titlePaddingStart,
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    collapseFraction
                )

                val titleYSecondInterpolatedPoint = lerp(
                    headerHeight / 2,
                    toolbarHeight / 2 - titleHeightPx.toDp() / 2,
                    collapseFraction
                )

                val titleXSecondInterpolatedPoint = lerp(
                    (titlePaddingEnd - titleExtraStartPadding) * 5 / 4,
                    titlePaddingEnd - titleExtraStartPadding,
                    collapseFraction
                )

                val titleY = lerp(
                    titleYFirstInterpolatedPoint,
                    titleYSecondInterpolatedPoint,
                    collapseFraction
                )

                val titleX = lerp(
                    titleXFirstInterpolatedPoint,
                    titleXSecondInterpolatedPoint,
                    collapseFraction
                )

                translationY = titleY.toPx()
                translationX = titleX.toPx()
                scaleX = scaleXY.value
                scaleY = scaleXY.value
            }
            .onGloballyPositioned {
                titleHeightPx = it.size.height.toFloat()
                titleWidthPx = it.size.width.toFloat()
            }
    )
}
