@file:OptIn(ExperimentalMaterialApi::class)

package com.mnnit.moticlubs.ui.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.mnnit.moticlubs.compressBitmap
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.ClubDetailModel
import com.mnnit.moticlubs.network.model.UpdateClubModel
import com.mnnit.moticlubs.network.model.UrlResponseModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var clubModel by mutableStateOf(savedStateHandle.get<ClubDetailModel>("clubDetail") ?: ClubDetailModel())
    var isFetching by mutableStateOf(false)
    var progressMsg by mutableStateOf("")

    val showSocialLinkDialog = mutableStateOf(false)
    val showOtherLinkDialog = mutableStateOf(false)
    val showProgressDialog = mutableStateOf(false)
    val showColorPaletteDialog = mutableStateOf(false)

    val otherLinks = mutableStateListOf<UrlResponseModel>()
    val otherLinksLiveList = mutableStateListOf<OtherLinkComposeModel>()
    val otherLinkIdx = mutableStateOf(0)

    val socialLinksLiveList = mutableStateListOf(
        SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel(), SocialLinkComposeModel()
    )
    val socialLinks = mutableStateListOf(
        UrlResponseModel(), UrlResponseModel(), UrlResponseModel(), UrlResponseModel()
    )

    var isAdmin = false

    fun pushUrls(
        _list: List<UrlResponseModel>,
        onResponse: () -> Unit,
        onFailure: (code: Int) -> Unit,
    ) {
        viewModelScope.launch {
            val clubID = clubModel.id
            val list = _list.map { it.mapToUrlModel() }
            Log.d("TAG", "pushUrls: ${Gson().toJson(list)}")
            val response = withContext(Dispatchers.IO) { repository.pushUrls(application, clubID, list) }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun updateProfilePic(url: String, onResponse: () -> Unit, onFailure: (code: Int) -> Unit) {
        viewModelScope.launch {
            val clubID = clubModel.id
            val response = withContext(Dispatchers.IO) {
                repository.updateClub(
                    application,
                    clubID,
                    UpdateClubModel(clubModel.description, url, clubModel.summary)
                )
            }
            if (response is Success) {
                onResponse()
            } else {
                onFailure(response.errCode)
            }
        }
    }

    fun fetchUrls() {
        isFetching = true

        viewModelScope.launch {
            val clubID = clubModel.id
            val response = withContext(Dispatchers.IO) { repository.getUrls(application, clubID) }
            if (response is Success) {
                val urls = response.obj

                socialLinks[0] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("facebook")
                } ?: UrlResponseModel()
                socialLinks[1] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("instagram")
                } ?: UrlResponseModel()
                socialLinks[2] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("twitter")
                } ?: UrlResponseModel()
                socialLinks[3] = urls.findLast {
                    it.name.toLowerCase(LocaleList.current).contains("github")
                } ?: UrlResponseModel()

                for (i in socialLinks.indices) {
                    socialLinksLiveList[i] = socialLinks[i].mapToSocialLinkModel()
                        .apply {
                            this.urlName = SocialLinkComposeModel.socialLinkNames[i]
                            this.clubID = clubModel.id
                        }
                }

                otherLinks.clear()
                otherLinks.addAll(urls.filter { f ->
                    !SocialLinkComposeModel.socialLinkNames.any { s -> f.name.contains(s) }
                })
                otherLinksLiveList.clear()
                otherLinksLiveList.addAll(otherLinks.map { m -> m.mapToOtherLinkModel() })
            } else {
                Toast.makeText(application, "${response.errCode}: Error couldn't load links", Toast.LENGTH_LONG).show()
            }
            isFetching = false
        }
    }

    init {
        fetchUrls()
    }
}

@Composable
fun ClubDetailsScreen(
    appViewModel: AppViewModel,
    viewModel: ClubDetailsScreenViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val colorScheme = getColorScheme()
    viewModel.isAdmin = appViewModel.user.admin.any { m -> m.clubID == viewModel.clubModel.id }

    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.isFetching,
        onRefresh = viewModel::fetchUrls
    )

    val context = LocalContext.current

    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme(2.dp, false)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Scaffold(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (viewModel.showProgressDialog.value) {
                    ProgressDialog(progressMsg = viewModel.progressMsg)
                }

                if (viewModel.showSocialLinkDialog.value) {
                    InputSocialLinkDialog(viewModel = viewModel) { list ->
                        handleUrls(viewModel, context, list)
                    }
                }

                if (viewModel.showOtherLinkDialog.value) {
                    InputOtherLinkDialog(viewModel = viewModel) { list ->
                        handleUrls(viewModel, context, list)
                    }
                }

                if (viewModel.showColorPaletteDialog.value) {
                    ColorPaletteDialog(
                        otherLinkComposeModel = viewModel.otherLinksLiveList[viewModel.otherLinkIdx.value],
                        viewModel.showColorPaletteDialog
                    )
                }

                Column(
                    modifier = Modifier
                        .pullRefresh(state = refreshState)
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = viewModel.isFetching || refreshState.progress.dp.value > 0.5f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.surfaceColorAtElevation(2.dp))
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .pullRefresh(state = refreshState)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            ClubProfilePic(
                                viewModel = viewModel,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = viewModel.clubModel.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                modifier = Modifier.padding(top = 0.dp),
                                text = viewModel.clubModel.summary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                modifier = Modifier.padding(),
                                text = "${viewModel.clubModel.subscribers} Members",
                                fontSize = 12.sp
                            )

                            val socials = viewModel.socialLinks.filter { f -> f.name.isNotEmpty() }
                            if (socials.isNotEmpty() || viewModel.isAdmin) {
                                Links(
                                    isAdmin = viewModel.isAdmin,
                                    "Socials",
                                    socials,
                                    onClick = {
                                        for (i in viewModel.socialLinks.indices) {
                                            viewModel.socialLinksLiveList[i] =
                                                viewModel.socialLinks[i].mapToSocialLinkModel()
                                                    .apply {
                                                        this.urlName = SocialLinkComposeModel.socialLinkNames[i]
                                                        this.clubID = viewModel.clubModel.id
                                                    }
                                        }
                                        viewModel.showSocialLinkDialog.value = true
                                    }
                                )
                            }
                            if (viewModel.otherLinks.isNotEmpty() || viewModel.isAdmin) {
                                Links(
                                    isAdmin = viewModel.isAdmin,
                                    "Others",
                                    viewModel.otherLinks,
                                    onClick = {
                                        viewModel.otherLinksLiveList.clear()
                                        viewModel.otherLinksLiveList.addAll(
                                            viewModel.otherLinks.map { m -> m.mapToOtherLinkModel() }
                                        )
                                        viewModel.showOtherLinkDialog.value = true
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        Text(viewModel.clubModel.description, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun InputSocialLinkDialog(viewModel: ClubDetailsScreenViewModel, onClick: (list: List<UrlResponseModel>) -> Unit) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { viewModel.showSocialLinkDialog.value = false }, DialogProperties()) {
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
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksLiveList[0].urlFieldValue.value,
                    onValueChange = { viewModel.socialLinksLiveList[0].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Facebook") },
                    singleLine = true,
                    isError = viewModel.socialLinksLiveList[0].getUrl().isNotEmpty()
                            && !viewModel.socialLinksLiveList[0].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksLiveList[1].urlFieldValue.value,
                    onValueChange = { viewModel.socialLinksLiveList[1].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Instagram") },
                    singleLine = true,
                    isError = viewModel.socialLinksLiveList[1].getUrl().isNotEmpty()
                            && !viewModel.socialLinksLiveList[1].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksLiveList[2].urlFieldValue.value,
                    onValueChange = { viewModel.socialLinksLiveList[2].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Twitter") },
                    singleLine = true,
                    isError = viewModel.socialLinksLiveList[2].getUrl().isNotEmpty()
                            && !viewModel.socialLinksLiveList[2].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksLiveList[3].urlFieldValue.value,
                    onValueChange = { viewModel.socialLinksLiveList[3].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Github") },
                    singleLine = true,
                    isError = viewModel.socialLinksLiveList[3].getUrl().isNotEmpty()
                            && !viewModel.socialLinksLiveList[3].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        val list = viewModel.socialLinksLiveList
                            .filter { it.validUrl() }.map { it.mapToUrlModel() }
                            .toMutableList()
                        val others = viewModel.otherLinksLiveList
                            .filter { it.validUrl() && it.getName().isNotEmpty() }.map { it.mapToUrlModel() }
                        list.addAll(others)
                        onClick(list)
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.socialLinksLiveList.any { it.validUrl() }
                ) {
                    Text(text = "Save Link(s)", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InputOtherLinkDialog(viewModel: ClubDetailsScreenViewModel, onClick: (list: List<UrlResponseModel>) -> Unit) {
    val colorScheme = getColorScheme()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    Dialog(
        onDismissRequest = { viewModel.showOtherLinkDialog.value = false },
        DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .animateContentSize()
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(128.dp, 512.dp)
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Text(
                    "Other Link Input",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold
                )

                if (viewModel.otherLinksLiveList.isEmpty()) {
                    viewModel.otherLinksLiveList.add(OtherLinkComposeModel())
                }

                LazyColumn(
                    state = listState, modifier = Modifier
                        .weight(1f, false)
                        .animateContentSize()
                ) {
                    items(viewModel.otherLinksLiveList.size) { idx ->
                        OtherLinkItem(
                            modifier = Modifier.animateItemPlacement(),
                            idx,
                            viewModel.otherLinksLiveList,
                            viewModel.otherLinkIdx,
                            viewModel.showColorPaletteDialog
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
                            viewModel.otherLinksLiveList.add(OtherLinkComposeModel())
                            scope.launch { listState.animateScrollToItem(viewModel.otherLinksLiveList.size - 1) }
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
                            val list = viewModel.socialLinksLiveList
                                .filter { it.validUrl() }.map { it.mapToUrlModel() }
                                .toMutableList()
                            val others = viewModel.otherLinksLiveList
                                .filter { it.validUrl() && it.getName().isNotEmpty() }.map { it.mapToUrlModel() }
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

@Composable
fun ClubProfilePic(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.showProgressDialog.value = true
            updateProfilePicture(context, result.uriContent!!, viewModel, viewModel.showProgressDialog)
        } else {
            val exception = result.error
            Toast.makeText(context, "Error ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(1, 1)
        imageCropLauncher.launch(cropOptions)
    }

    Row(modifier = modifier) {
        ProfilePicture(
            modifier = Modifier.padding(start = if (viewModel.isAdmin) 46.dp else 0.dp),
            url = viewModel.clubModel.avatar,
            size = 156.dp
        )

        if (viewModel.isAdmin) {
            IconButton(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .border(1.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
            ) {
                Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
            }
        }
    }
}

private fun updateProfilePicture(
    context: Context,
    imageUri: Uri,
    viewModel: ClubDetailsScreenViewModel,
    loading: MutableState<Boolean>
) {
    val storageRef = Firebase.storage.reference
    val profilePicRef =
        storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

    val bitmap = compressBitmap(imageUri, context)
    bitmap ?: return

    val boas = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
    profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
        if (!task.isSuccessful) {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
        profilePicRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUrl = task.result.toString()
            viewModel.updateProfilePic(downloadUrl, {
                loading.value = false
                viewModel.clubModel.avatar = downloadUrl
            }) {
                loading.value = false
                Toast.makeText(context, "Error setting profile picture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            loading.value = false
        }
    }
}

