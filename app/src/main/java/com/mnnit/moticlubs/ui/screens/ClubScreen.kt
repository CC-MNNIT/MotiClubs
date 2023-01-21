@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.api.PostResponse
import com.mnnit.moticlubs.api.UserDetailResponse
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubScreenViewModel @Inject constructor() : ViewModel() {

    val postMsg = mutableStateOf("")
    val postsList = mutableStateListOf<PostResponse>()
    val clubModel = mutableStateOf(ClubModel("", "", "", "", listOf()))

    val selected = mutableStateOf(false)
    val bottomSheetScaffoldState = mutableStateOf(
        BottomSheetScaffoldState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            bottomSheetState = BottomSheetState(initialValue = BottomSheetValue.Expanded),
            snackbarHostState = SnackbarHostState()
        )
    )
    val scrollValue = mutableStateOf(0)

    fun fetchPostsList(context: Context) {
        viewModelScope.launch {
            API.getClubPosts(context.getAuthToken(), clubID = clubModel.value.id, { list ->
                postsList.clear()
                list.forEach { postsList.add(it) }
            }) {}
        }
    }
}

@Composable
fun ClubScreen(
    _clubModel: ClubModel,
    appViewModel: AppViewModel,
    viewModel: ClubScreenViewModel = hiltViewModel()
) {
    viewModel.clubModel.value = _clubModel
    viewModel.bottomSheetScaffoldState.value = BottomSheetScaffoldState(
        drawerState = DrawerState(initialValue = DrawerValue.Closed),
        bottomSheetState = BottomSheetState(
            initialValue = if (viewModel.clubModel.value.admins.contains(appViewModel.email.value)) {
                BottomSheetValue.Expanded
            } else {
                BottomSheetValue.Collapsed
            }
        ),
        snackbarHostState = SnackbarHostState()
    )
    viewModel.fetchPostsList(LocalContext.current)

    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        Surface(modifier = Modifier.imePadding(), color = colorScheme.background) {
            BottomSheetScaffold(modifier = Modifier.imePadding(), sheetContent = {
                BottomSheetContent(viewModel)
            }, topBar = {
                Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                    ChannelNameBar(
                        viewModel,
                        modifier = Modifier.padding(top = appViewModel.paddingValues.value.top())
                    )
                }
            }, content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.background)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        Messages(
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f),
                            scrollState = listScrollState,
                            appViewModel = appViewModel
                        )
                    }
                }
            }, scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = if (viewModel.clubModel.value.admins.contains(appViewModel.email.value)) {
                    204.dp + appViewModel.paddingValues.value.bottom()
                } else {
                    0.dp
                }, sheetBackgroundColor = colorScheme.surfaceColorAtElevation(2.dp)
            )
        }
    }
}

@Composable
private fun BottomSheetContent(viewModel: ClubScreenViewModel) {
    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val colorScheme = getColorScheme()
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp, end = 16.dp
                )
                .imePadding()
                .fillMaxWidth()
                .heightIn(0.dp, 480.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(4.dp))
                    .background(contentColorFor(backgroundColor = colorScheme.background))
            ) {
                Text(text = "", modifier = Modifier.padding(12.dp))
            }

            Text(
                text = "Write Post",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .imePadding()
                    .fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = viewModel.selected.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    MarkdownText(
                        markdown = viewModel.postMsg.value,
                        color = contentColorFor(backgroundColor = colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AnimatedVisibility(visible = !viewModel.selected.value) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    scope.launch {
                                        if (viewModel.bottomSheetScaffoldState.value.bottomSheetState.isCollapsed) {
                                            viewModel.scrollValue.value = scrollState.value
                                            viewModel.bottomSheetScaffoldState.value.bottomSheetState.expand()
                                        }
                                    }
                                }
                            },
                        value = viewModel.postMsg.value,
                        onValueChange = {
                            val scrollValue = viewModel.scrollValue.value +
                                    (scrollMultiplierIndex(viewModel.postMsg.value, it) * 53)

                            viewModel.postMsg.value = it
                            scope.launch {
                                scrollState.animateScrollTo(scrollValue)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(text = "Post") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
                            disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background)
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .imePadding()
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FilterChip(
                        selected = viewModel.selected.value,
                        onClick = {
                            viewModel.selected.value = !viewModel.selected.value
                            if (viewModel.selected.value) {
                                scope.launch {
                                    scrollState.animateScrollTo(0)
                                }
                            }
                            keyboardController?.hide()
                        },
                        label = {
                            Text(text = "Preview", fontSize = 14.sp)
                        }, leadingIcon = {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Visibility),
                                contentDescription = ""
                            )
                        }, modifier = Modifier
                            .imePadding()
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "Send",
                                fontSize = 14.sp,
                                color = contentColorFor(
                                    backgroundColor = if (viewModel.postMsg.value.isNotEmpty()) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            )
                        }, leadingIcon = {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Rounded.Send),
                                contentDescription = "",
                                tint = contentColorFor(
                                    backgroundColor = if (viewModel.postMsg.value.isNotEmpty()) {
                                        colorScheme.primary
                                    } else {
                                        colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            )
                        }, modifier = Modifier
                            .imePadding()
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(24.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primary),
                        enabled = viewModel.postMsg.value.isNotEmpty()
                    )
                }
            }
        }
    }
}

private fun scrollMultiplierIndex(prev: String, curr: String): Int {
    val q: String
    val p: String
    if (curr.length > prev.length) {
        q = prev
        p = curr
    } else {
        q = curr
        p = prev
    }
    var breakLines = 0
    q.forEachIndexed { index, c ->
        if (p[index] != c) {
            for (i in 0..index) {
                if (p[i] == '\n') breakLines++
            }
            return breakLines
        }
    }
    curr.forEach {
        if (it == '\n') breakLines++
    }
    return breakLines
}

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(64.dp)
                .padding(16.dp)
                .align(Alignment.CenterVertically),
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = ""
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Channel name
            Text(
                text = viewModel.clubModel.value.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )

            // Number of members
            Text(
                text = viewModel.clubModel.value.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(64.dp), contentDescription = ""
            )
            // Info icon
            Icon(
                imageVector = Icons.Outlined.Info,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
                    .height(64.dp),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun Messages(
    viewModel: ClubScreenViewModel,
    scrollState: LazyListState,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = appViewModel.paddingValues.value.bottom() +
                        if (viewModel.clubModel.value.admins.contains(appViewModel.email.value)) {
                            194.dp
                        } else {
                            0.dp
                        }
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            viewModel.postsList.forEach { post ->
                item {
                    Message(post = post, admin = appViewModel.adminInfoMap[post.adminEmail] ?: UserDetailResponse())
                }
            }
        }
    }
}

@Composable
fun Message(
    post: PostResponse,
    admin: UserDetailResponse,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp), elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .align(Alignment.Top),

                painter = if (admin.avatar.isEmpty()) {
                    painterResource(id = R.drawable.outline_account_circle_24)
                } else {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(admin.avatar)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(admin.personalEmail)
                            .error(R.drawable.outline_account_circle_24)
                            .placeholder(R.drawable.outline_account_circle_24)
                            .build()
                    )
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            AuthorAndTextMessage(
                post = post,
                name = admin.name,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun AuthorAndTextMessage(
    post: PostResponse,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AuthorNameTimestamp(post, name)
        Column {
            MarkdownText(
                markdown = post.message,
                color = contentColorFor(backgroundColor = getColorScheme().background),
                maxLines = 1
            )
        }
        Spacer(
            modifier = Modifier
                .height(5.dp)
                .background(color = Color.White)
        )
    }
}

@Composable
private fun AuthorNameTimestamp(post: PostResponse, name: String) {
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = post.time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            fontSize = 10.sp
        )
    }
}
