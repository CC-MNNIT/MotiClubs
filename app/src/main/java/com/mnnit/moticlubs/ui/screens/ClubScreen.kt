@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mnnit.moticlubs.*
import com.mnnit.moticlubs.api.*
import com.mnnit.moticlubs.ui.ConfirmationDialog
import com.mnnit.moticlubs.ui.ProgressDialog
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.getImageUrlPainter
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ClubScreenViewModel @Inject constructor() : ViewModel() {

    val postMsg = mutableStateOf(TextFieldValue(""))
    val postsList = mutableStateListOf<PostResponse>()
    val clubModel = mutableStateOf(ClubModel("", "", "", "", listOf()))

    val isPreviewMode = mutableStateOf(false)
    val isMemberPost = mutableStateOf(false)

    val inputLinkName = mutableStateOf("")
    val inputLink = mutableStateOf("")
    val showLinkDialog = mutableStateOf(false)

    val progressText = mutableStateOf("Loading")
    val showProgress = mutableStateOf(false)
    val showDialog = mutableStateOf(false)
    val showSubsDialog = mutableStateOf(false)

    val subscribed = mutableStateOf(false)
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
    viewModel.subscribed.value = appViewModel.subscribedList.contains(viewModel.clubModel.value.id)

    val listScrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        SetNavBarsTheme(elevation = 2.dp, viewModel.subscribed.value)
        Surface(modifier = Modifier.imePadding(), color = colorScheme.background) {
            BottomSheetScaffold(modifier = Modifier.imePadding(), sheetContent = {
                BottomSheetContent(viewModel)
            }, topBar = {
                Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                    ChannelNameBar(
                        viewModel,
                        appViewModel,
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
            ProgressDialog(progressMsg = viewModel.progressText.value)
        }

        if (viewModel.showDialog.value) {
            PostConfirmationDialog(viewModel = viewModel) {
                viewModel.isPreviewMode.value = false
                API.sendPost(context.getAuthToken(), viewModel.clubModel.value.id, viewModel.postMsg.value.text, {
                    Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
                    viewModel.fetchPostsList(context)

                    viewModel.showProgress.value = false
                    viewModel.postMsg.value = TextFieldValue("")
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
                        markdown = viewModel.postMsg.value.text,
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
                                    (scrollMultiplierIndex(viewModel.postMsg.value.text, it.text) * 53)

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

                TextFormatter(viewModel = viewModel)

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

                    FilterChip(
                        selected = viewModel.isMemberPost.value,
                        onClick = { viewModel.isMemberPost.value = !viewModel.isMemberPost.value },
                        label = {
                            Text(text = if (viewModel.isMemberPost.value) "Members" else "General", fontSize = 14.sp)
                        }, leadingIcon = {
                            Icon(
                                painter = rememberVectorPainter(
                                    image = if (viewModel.isMemberPost.value) {
                                        Icons.Rounded.Group
                                    } else Icons.Rounded.Groups3
                                ),
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
                                    backgroundColor = if (viewModel.postMsg.value.text.isNotEmpty()) {
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
                                    backgroundColor = if (viewModel.postMsg.value.text.isNotEmpty()) {
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
                        enabled = viewModel.postMsg.value.text.isNotEmpty()
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
private fun TextFormatter(viewModel: ClubScreenViewModel) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = {
        if (it == null) {
            Toast.makeText(context, "Image not selected", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        uploadPostPic(context, it, viewModel) { url ->
            val post = viewModel.postMsg.value.text
            val selection = viewModel.postMsg.value.selection
            val urlLink = "\n![post_img]($url)\n"
            viewModel.postMsg.value = TextFieldValue(
                post.replaceRange(selection.start, selection.end, urlLink),
                selection = TextRange(selection.end + urlLink.length, selection.end + urlLink.length)
            )
        }
    })

    if (viewModel.showLinkDialog.value) {
        InputLinkDialog(viewModel = viewModel)
    }

    AnimatedVisibility(visible = !viewModel.isPreviewMode.value) {
        Row(
            modifier = Modifier
                .imePadding()
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { formatMsg(viewModel, "**") }) {
                Icon(Icons.Rounded.FormatBold, contentDescription = "")
            }

            IconButton(onClick = { formatMsg(viewModel, "_") }) {
                Icon(Icons.Rounded.FormatItalic, contentDescription = "")
            }

            IconButton(onClick = { formatMsg(viewModel, "~~") }) {
                Icon(Icons.Rounded.FormatStrikethrough, contentDescription = "")
            }

            IconButton(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Rounded.InsertPhoto, contentDescription = "")
            }

            IconButton(onClick = { viewModel.showLinkDialog.value = true }) {
                Icon(Icons.Rounded.InsertLink, contentDescription = "")
            }
        }
    }
}

private fun formatMsg(viewModel: ClubScreenViewModel, token: String) {
    val str = viewModel.postMsg.value.text
    val tr = viewModel.postMsg.value.selection
    val subStr = str.substring(tr.start, tr.end)
    if (subStr.isEmpty()) return

    val offset = token.length * 2
    viewModel.postMsg.value = TextFieldValue(
        str.replaceRange(tr.start, tr.end, "$token$subStr$token"),
        selection = TextRange(tr.end + offset, tr.end + offset)
    )
}

private fun uploadPostPic(
    context: Context,
    imageUri: Uri,
    viewModel: ClubScreenViewModel,
    onUploaded: (url: String) -> Unit
) {
    viewModel.showProgress.value = true
    viewModel.progressText.value = "Uploading ..."

    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("post_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(System.currentTimeMillis().toString())

    val bitmap = compressBitmap(imageUri, context)
    bitmap ?: return

    val boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
    profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
        if (!task.isSuccessful) {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            viewModel.showProgress.value = false
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            viewModel.showProgress.value = false
            onUploaded(downloadUri.toString())
        } else {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            viewModel.showProgress.value = false
        }
    }
}

@Composable
fun PostConfirmationDialog(viewModel: ClubScreenViewModel, onPost: () -> Unit) {
    ConfirmationDialog(
        showDialog = viewModel.showDialog,
        message = "Post message in ${viewModel.clubModel.value.name} ?", positiveBtnText = "Post",
        imageVector = Icons.Outlined.Article,
        onPositive = {
            viewModel.showProgress.value = true
            onPost()
        }
    )
}

@Composable
fun InputLinkDialog(viewModel: ClubScreenViewModel) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showLinkDialog.value = false }, DialogProperties()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Link Input",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputLinkName.value,
                    onValueChange = { viewModel.inputLinkName.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.inputLink.value,
                    onValueChange = { viewModel.inputLink.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Link") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                TextButton(
                    onClick = {
                        val post = viewModel.postMsg.value.text
                        val selection = viewModel.postMsg.value.selection
                        val link = "\n[${viewModel.inputLinkName.value}](${viewModel.inputLink.value})\n"
                        viewModel.postMsg.value = TextFieldValue(
                            post.replaceRange(selection.start, selection.end, link),
                            selection = TextRange(selection.end + link.length, selection.end + link.length)
                        )
                        viewModel.showLinkDialog.value = false
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.inputLink.value.matches(Patterns.WEB_URL.toRegex())
                ) {
                    Text(
                        text = "Add link",
                        color = if (viewModel.inputLink.value.matches(Patterns.WEB_URL.toRegex())) {
                            colorScheme.primary
                        } else colorScheme.contentColorFor(colorScheme.background),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelNameBar(
    viewModel: ClubScreenViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onNavigateToClubDetails: () -> Unit
) {
    if (viewModel.showSubsDialog.value) {
        SubscriptionConfirmationDialog(
            viewModel = viewModel,
            appViewModel = appViewModel,
            subscribe = !viewModel.subscribed.value
        )
    }
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
                imageVector = if (viewModel.subscribed.value) {
                    Icons.Rounded.NotificationsActive
                } else Icons.Outlined.NotificationsOff,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
                    .height(64.dp)
                    .clip(CircleShape)
                    .clickable {
                        viewModel.showSubsDialog.value = true
                    },
                contentDescription = ""
            )
        }
    }
}

@Composable
fun SubscriptionConfirmationDialog(
    viewModel: ClubScreenViewModel, appViewModel: AppViewModel,
    subscribe: Boolean
) {
    val context = LocalContext.current

    ConfirmationDialog(
        showDialog = viewModel.showSubsDialog,
        message = "Are you sure you want to ${if (subscribe) "subscribe" else "unsubscribe"} ?",
        positiveBtnText = if (subscribe) "Subscribe" else "Unsubscribe",
        imageVector = if (subscribe) Icons.Rounded.NotificationsActive else Icons.Outlined.NotificationsOff,
        onPositive = {
            viewModel.showProgress.value = true
            if (subscribe) {
                API.subscribeToClub(context.getAuthToken(), viewModel.clubModel.value.id, {
                    appViewModel.subscribedList.add(viewModel.clubModel.value.id)
                    viewModel.showProgress.value = false
                    viewModel.subscribed.value = appViewModel.subscribedList.contains(viewModel.clubModel.value.id)
                    Toast.makeText(context, "Subscribed", Toast.LENGTH_SHORT).show()
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error could not process request", Toast.LENGTH_SHORT).show()
                }
            } else {
                API.unsubscribeToClub(context.getAuthToken(), viewModel.clubModel.value.id, {
                    appViewModel.subscribedList.remove(viewModel.clubModel.value.id)
                    viewModel.showProgress.value = false
                    viewModel.subscribed.value = appViewModel.subscribedList.contains(viewModel.clubModel.value.id)
                    Toast.makeText(context, "Unsubscribed", Toast.LENGTH_SHORT).show()
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error could not process request", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
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
                    appViewModel,
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
    appViewModel: AppViewModel,
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
                    viewModel.clubModel.value.id,
                    viewModel.postsList[idx].id,
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
                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = LocalContext.current.getUnreadPost(viewModel.clubModel.value.id)
                        .contains(viewModel.postsList[idx].id),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                ) {
                    BadgedBox(badge = { Badge { } }) {}
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(visible = viewModel.postsList[idx].adminEmail == appViewModel.email.value) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "")
                    }
                }
                AnimatedVisibility(visible = viewModel.postsList[idx].adminEmail == appViewModel.email.value) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "")
                    }
                }
            }
        }
        MarkdownText(
            markdown = viewModel.postsList[idx].message,
            color = contentColorFor(backgroundColor = getColorScheme().background),
            maxLines = 4,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp, top = 8.dp),
            disableLinkMovementMethod = true
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
