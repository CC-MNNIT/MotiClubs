package com.mnnit.moticlubs.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.api.API
import com.mnnit.moticlubs.api.ClubModel
import com.mnnit.moticlubs.api.PostResponse
import com.mnnit.moticlubs.api.UserDetailResponse
import com.mnnit.moticlubs.getAuthToken
import com.mnnit.moticlubs.toTimeString
import com.mnnit.moticlubs.top
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubScreenViewModel @Inject constructor() : ViewModel() {

    val postsList = mutableStateListOf<PostResponse>()
    val selected = mutableStateOf(false)
    val clubModel = mutableStateOf(ClubModel("", "", "", "", listOf()))

    fun fetchPostsList(context: Context, clubID: String) {
        API.getClubPosts(context.getAuthToken(), clubID = clubID, { list ->
            postsList.clear()
            list.forEach { postsList.add(it) }
        }) {}
    }
}

@Composable
fun ClubScreen(
    _clubModel: ClubModel,
    appViewModel: AppViewModel,
    viewModel: ClubScreenViewModel = hiltViewModel()
) {
    viewModel.clubModel.value = _clubModel
    val context = LocalContext.current
    viewModel.fetchPostsList(context, viewModel.clubModel.value.id)

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    val colorScheme = getColorScheme()
    MotiClubsTheme(colorScheme) {
        Surface(modifier = Modifier, color = colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    Messages(
                        posts = viewModel.postsList,
                        modifier = Modifier.weight(1f),
                        scrollState = scrollState,
                        appViewModel = appViewModel
                    )

                    UserInput(
                        viewModel,
                        onMessageSent = {
                        },
                        resetScroll = {
                            scope.launch { scrollState.scrollToItem(0) }
                        },
                        // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
                        // navigation bar, and on-screen keyboard (IME)
                        modifier = Modifier
                            .padding()
                            .imePadding(),
                    )
                }
                // Channel name bar floats above the messages
                Surface(color = colorScheme.background, tonalElevation = 2.dp) {
                    ChannelNameBar(
                        clubName = viewModel.clubModel.value.name,
                        clubDesc = viewModel.clubModel.value.description,
                        modifier = Modifier.padding(top = appViewModel.paddingValues.value.top())
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelNameBar(
    clubName: String,
    clubDesc: String,
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
                text = clubName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )

            // Number of members
            Text(
                text = clubDesc,
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

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun Messages(
    posts: List<PostResponse>,
    scrollState: LazyListState,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(top = appViewModel.paddingValues.value.top() + 90.dp),
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            posts.forEach { post ->
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
//        Spacer(modifier = Modifier.height(2.dp))
        Column {
            MarkdownText(
                markdown = post.message,
                color = contentColorFor(backgroundColor = getColorScheme().background)
            )
        }
        Spacer(modifier = Modifier
            .height(5.dp)
            .background(color = Color.White))
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
