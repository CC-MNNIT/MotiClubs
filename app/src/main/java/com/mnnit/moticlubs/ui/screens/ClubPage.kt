package com.mnnit.moticlubs.ui.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.conversation.UserInput
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.*
import com.mnnit.moticlubs.getAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ClubScreenViewModel @Inject constructor() : ViewModel() {

    val postsList = mutableStateListOf<PostResponse>()

    fun setPostsList(list: List<PostResponse>) {
        postsList.clear()
        list.forEach { postsList.add(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen(
    clubScreenViewModel: ClubScreenViewModel = hiltViewModel(),
    clubModel: ClubModel,
    modifier: Modifier
) {
    val context = LocalContext.current
    API.getClubPosts(context.getAuthToken(), clubID = clubModel.id, {
        clubScreenViewModel.setPostsList(it)
    }) {}

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    androidx.compose.material3.Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Messages(
                    posts = clubScreenViewModel.postsList,
//                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState,
                    context = context
                )

                UserInput(
//                    onMessageSent = { content ->
//                        uiState.addMessage(
//                            Message(authorMe, content, timeNow)
//                        )
//                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
                    // navigation bar, and on-screen keyboard (IME)
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding(),
                )
            }
            // Channel name bar floats above the messages
            ChannelNameBar(
                clubName = clubModel.name,
                clubDesc = clubModel.description,
//                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}

@Composable
fun ChannelNameBar(
    clubName: String,
    clubDesc: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
//    onNavIconPressed: () -> Unit = { }
) {

    JetchatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
//        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                androidx.compose.material3.Text(
                    text = clubName,
                    style = MaterialTheme.typography.titleMedium
                )
                // Number of members
                androidx.compose.material3.Text(
                    text = clubDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Search icon
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
//                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp), contentDescription = ""
            )
            // Info icon
            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.Info,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
//                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = ""
            )
        }
    )
}

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun Messages(
    posts: List<PostResponse>,
//    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    context: Context
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            for (index in posts.indices) {
                val post = posts[index]
                var name: String = ""
                API.getUserDetails(context.getAuthToken(), post.adminEmail, {
                    name = it.name
                }) {}

                item {
                    Message(
//                        onAuthorClick = { name -> navigateToProfile(name) },
                        post = post,
                        name = name
                    )
                }
            }
        }
    }
}

@Composable
fun Message(
//    onAuthorClick: (String) -> Unit,
    post: PostResponse,
    name: String
) {

    val borderColor = MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), onClick = {/* TODO */ },
        shape = ChatBubbleShape, elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row() {
            Image(
                modifier = Modifier
//                .clickable(onClick = { onAuthorClick(msg.author) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                painter = painterResource(id = R.drawable.someone_else),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            AuthorAndTextMessage(
                post = post,
                name = name,
//            authorClicked = onAuthorClick,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun AuthorAndTextMessage(
    post: PostResponse,
    name: String,
//    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AuthorNameTimestamp(post, name)
//        Spacer(modifier = Modifier.height(2.dp))
        ChatItemBubble(post)
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
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = post.time.toTimeString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp

        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun ChatItemBubble(
    post: PostResponse,
//    authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor =
        MaterialTheme.colorScheme.surfaceVariant

    Column(Modifier.padding(16.dp)) {
        androidx.compose.material3.Surface(
            color = backgroundBubbleColor
        ) {
            ClickableMessage(
                post = post,
//                authorClicked = authorClicked
            )
        }
    }
}

private val mMonthsList: List<String> = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
    "Aug", "Sep", "Oct", "Nov", "Dec"
)

fun Long.toTimeString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this

    val hour = calendar.get(Calendar.HOUR)
    val min = calendar.get(Calendar.MINUTE)
    val amPm = calendar.get(Calendar.AM_PM)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)

    return "${if (hour < 10) "0$hour" else "$hour"}:${if (min < 10) "0$min" else "$min"} " +
            "${if (amPm == Calendar.AM) "AM" else "PM"}, $day ${mMonthsList[month]}"
}

@Composable
fun ClickableMessage(
    post: PostResponse,
//    authorClicked: (String) -> Unit
) {
    Text(text = post.message)
    /*
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = post.message,
        primary = isUserMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = androidx.compose.material3.LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )

     */
}

private fun ScrollState.atBottom(): Boolean = value == 0
