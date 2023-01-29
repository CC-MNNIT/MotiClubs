@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.api.*
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
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

    val isPreviewMode = mutableStateOf(false)
    val showProgress = mutableStateOf(false)
    val showDialog = mutableStateOf(false)
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
    appViewModel: AppViewModel,
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    onNavigateToClubDetails: () -> Unit,
    viewModel: ClubScreenViewModel = hiltViewModel()
) {
    viewModel.clubModel.value = appViewModel.clubModel.value
    viewModel.bottomSheetScaffoldState.value = rememberBottomSheetScaffoldState()
    viewModel.fetchPostsList(LocalContext.current)

    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(elevation = 2.dp, viewModel.clubModel.value.admins.contains(appViewModel.email.value))
        Surface(modifier = Modifier.imePadding(), color = colorScheme.background) {
            BottomSheetScaffold(modifier = Modifier.imePadding(), sheetContent = {
                BottomSheetContent(viewModel)
            }, topBar = {
                Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                    ChannelNameBar(
                        viewModel,
                        modifier = Modifier.padding(),
                        onNavigateToClubDetails = onNavigateToClubDetails
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
                            appViewModel = appViewModel,
                            onNavigateToPost = onNavigateToPost
                        )
                    }
                }
            }, scaffoldState = viewModel.bottomSheetScaffoldState.value,
                sheetPeekHeight = if (viewModel.clubModel.value.admins.contains(appViewModel.email.value)) {
                    72.dp
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
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Surface(
        color = colorScheme.background,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        if (viewModel.showProgress.value) {
            Dialog(
                onDismissRequest = {},
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = colorScheme.primary)
            }
        }
        if (viewModel.showDialog.value) {
            PostConfirmationDialog(viewModel = viewModel) {
                viewModel.isPreviewMode.value = false
                API.sendPost(context.getAuthToken(), viewModel.clubModel.value.id, viewModel.postMsg.value, {
                    Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                    viewModel.fetchPostsList(context)

                    viewModel.showProgress.value = false
                    viewModel.postMsg.value = ""
                    scope.launch {
                        viewModel.bottomSheetScaffoldState.value.bottomSheetState.collapse()
                    }
                }) { Toast.makeText(context, "$it: Error posting msg", Toast.LENGTH_SHORT).show() }
            }
        }
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp, end = 16.dp
                )
                .imePadding()
                .fillMaxWidth()
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
                    visible = viewModel.isPreviewMode.value,
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

                AnimatedVisibility(visible = !viewModel.isPreviewMode.value) {
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
                        selected = viewModel.isPreviewMode.value,
                        onClick = {
                            viewModel.isPreviewMode.value = !viewModel.isPreviewMode.value
                            if (viewModel.isPreviewMode.value) {
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
                        onClick = {
                            keyboardController?.hide()
                            viewModel.showDialog.value = true
                            focusManager.clearFocus()
                        },
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
    val q = if (curr.length > prev.length) prev else curr
    val p = if (curr.length > prev.length) curr else prev
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
fun PostConfirmationDialog(viewModel: ClubScreenViewModel, onPost: () -> Unit) {
    val colorScheme = getColorScheme()
    AlertDialog(onDismissRequest = {
        viewModel.showDialog.value = false
    }, text = {
        Text(text = "Post message in ${viewModel.clubModel.value.name} ?", fontSize = 16.sp)
    }, confirmButton = {
        TextButton(onClick = {
            viewModel.showDialog.value = false
            viewModel.showProgress.value = true
            onPost()
        }) {
            Text(text = "Post", fontSize = 14.sp, color = colorScheme.primary)
        }
    }, dismissButton = {
        TextButton(onClick = { viewModel.showDialog.value = false }) {
            Text(text = "Cancel", fontSize = 14.sp, color = colorScheme.primary)
        }
    }, icon = {
        Icon(
            painter = rememberVectorPainter(image = Icons.Outlined.Article),
            contentDescription = "",
            modifier = Modifier.size(36.dp)
        )
    })
}

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(true, onClick = {
                onNavigateToClubDetails()
            }), horizontalArrangement = Arrangement.SpaceAround
    ) {
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
    onNavigateToPost: (post: PostNotificationModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = if (viewModel.clubModel.value.admins.contains(appViewModel.email.value)) {
                    72.dp
                } else {
                    0.dp
                }
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            items(viewModel.postsList.size) { index ->
                Message(
                    viewModel,
                    index,
                    admin = appViewModel.adminInfoMap[viewModel.postsList[index].adminEmail] ?: UserDetailResponse(),
                    onNavigateToPost
                )
            }
        }
    }
}

@Composable
fun Message(
    viewModel: ClubScreenViewModel,
    idx: Int,
    admin: UserDetailResponse,
    onNavigateToPost: (post: PostNotificationModel) -> Unit
) {
    val colorScheme = getColorScheme()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp), elevation = CardDefaults.cardElevation(0.dp),
        onClick = {
            onNavigateToPost(
                PostNotificationModel(
                    viewModel.clubModel.value.name,
                    admin.name,
                    admin.avatar,
                    viewModel.postsList[idx].message,
                    viewModel.postsList[idx].time.toTimeString()
                )
            )
        },
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(8.dp))
    ) {
        Card(
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .align(Alignment.Top),

                    painter = LocalContext.current.getImageUrlPainter(url = admin.avatar),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                AuthorNameTimestamp(viewModel.postsList[idx], admin.name)
            }
        }
        MarkdownText(
            markdown = viewModel.postsList[idx].message,
            color = contentColorFor(backgroundColor = getColorScheme().background),
            maxLines = 4,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 8.dp)
        )
    }
}

@Composable
private fun AuthorNameTimestamp(post: PostResponse, name: String) {
    Column(modifier = Modifier
        .padding(start = 16.dp)
        .semantics(mergeDescendants = true) {}) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = post.time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
