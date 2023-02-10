package com.mnnit.moticlubs.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
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
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.Success
import com.mnnit.moticlubs.network.model.ClubDetailModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.components.*
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetNavBarsTheme
import com.mnnit.moticlubs.ui.theme.getColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClubDetailsScreenViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var clubModel by mutableStateOf(savedStateHandle.get<ClubDetailModel>("clubDetail") ?: ClubDetailModel())

    val showSocialLinkDialog = mutableStateOf(false)
    val showOtherLinkDialog = mutableStateOf(false)
    val showColorPaletteDialog = mutableStateOf(false)
    val otherLinksList = mutableStateListOf<LinkComposeModel>()
    val otherLinkIdx = mutableStateOf(0)

    val socialLinksList =
        mutableStateListOf(LinkComposeModel(), LinkComposeModel(), LinkComposeModel(), LinkComposeModel())

    var isAdmin = false

    fun fetchUrls() {
        viewModelScope.launch {
            val clubID = clubModel.id
            val response = withContext(Dispatchers.IO) { repository.getUrls(application, clubID) }
            if (response is Success) {
                val urls = response.obj

                socialLinksList[0] = urls.findLast {
                    it.url.toLowerCase(LocaleList.current).contains("facebook")
                }?.map() ?: LinkComposeModel()

                socialLinksList[1] = urls.findLast {
                    it.url.toLowerCase(LocaleList.current).contains("instagram")
                }?.map() ?: LinkComposeModel()

                socialLinksList[2] = urls.findLast {
                    it.url.toLowerCase(LocaleList.current).contains("twitter")
                }?.map() ?: LinkComposeModel()

                socialLinksList[3] = urls.findLast {
                    it.url.toLowerCase(LocaleList.current).contains("github")
                }?.map() ?: LinkComposeModel()

                otherLinksList.clear()
                otherLinksList.addAll(
                    urls.filter { f ->
                        !LinkComposeModel.socialLinkNames.any { s -> f.url.contains(s) }
                    }.map { m -> m.map() }
                )
            } else {
                Toast.makeText(application, "${response.errCode}: Error couldn't load links", Toast.LENGTH_LONG).show()
            }
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

    MotiClubsTheme(colorScheme = getColorScheme()) {
        SetNavBarsTheme(2.dp, false)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Scaffold(
                modifier = Modifier.fillMaxWidth(),
                floatingActionButton = {
                    Row(modifier = Modifier) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Cancel", fontSize = 15.sp, textAlign = TextAlign.Center) },
                            icon = { Icon(imageVector = Icons.Outlined.Close, contentDescription = "") },
                            onClick = { },
                            shape = RoundedCornerShape(24.dp),
                            containerColor = colorScheme.errorContainer
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Save Changes", fontSize = 15.sp, textAlign = TextAlign.Center) },
                            icon = { Icon(imageVector = Icons.Outlined.Save, contentDescription = "") },
                            onClick = { },
                            shape = RoundedCornerShape(24.dp),
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.Center
            ) {
                if (viewModel.showSocialLinkDialog.value) {
                    InputSocialLinkDialog(viewModel = viewModel)
                }

                if (viewModel.showOtherLinkDialog.value) {
                    InputOtherLinkDialog(viewModel = viewModel)
                }

                if (viewModel.showColorPaletteDialog.value) {
                    ColorPaletteDialog(
                        linkComposeModel = viewModel.otherLinksList[viewModel.otherLinkIdx.value],
                        viewModel.showColorPaletteDialog
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            ClubProfilePic(
                                clubModel = viewModel.clubModel,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                modifier = Modifier.padding(top = 16.dp),
                                text = viewModel.clubModel.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                modifier = Modifier.padding(),
                                text = "${viewModel.clubModel.subscribers} Members",
                                fontSize = 12.sp
                            )

                            Links(
                                isAdmin = viewModel.isAdmin,
                                "Socials",
                                viewModel.socialLinksList.map { m ->
                                    LinkModel(m.getName(), m.getUrl(), m.colorCode.value.replace("#", ""))
                                }.filter { f -> f.name.isNotEmpty() },
                                onClick = {
                                    viewModel.showSocialLinkDialog.value = true
                                }
                            )
                            Links(
                                isAdmin = viewModel.isAdmin,
                                "Others",
                                viewModel.otherLinksList.map { m ->
                                    LinkModel(m.getName(), m.getUrl(), m.colorCode.value.replace("#", ""))
                                },
                                onClick = { viewModel.showOtherLinkDialog.value = true }
                            )
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
fun InputSocialLinkDialog(viewModel: ClubDetailsScreenViewModel) {
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
                    value = viewModel.socialLinksList[0].fieldValue.value,
                    onValueChange = { viewModel.socialLinksList[0].fieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Facebook") },
                    singleLine = true,
                    isError = viewModel.socialLinksList[0].getUrl().isNotEmpty()
                            && !viewModel.socialLinksList[0].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksList[1].fieldValue.value,
                    onValueChange = { viewModel.socialLinksList[1].fieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Instagram") },
                    singleLine = true,
                    isError = viewModel.socialLinksList[1].getUrl().isNotEmpty()
                            && !viewModel.socialLinksList[1].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksList[1].fieldValue.value,
                    onValueChange = { viewModel.socialLinksList[1].fieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Twitter") },
                    singleLine = true,
                    isError = viewModel.socialLinksList[1].getUrl().isNotEmpty()
                            && !viewModel.socialLinksList[1].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.socialLinksList[1].fieldValue.value,
                    onValueChange = { viewModel.socialLinksList[1].fieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Github") },
                    singleLine = true,
                    isError = viewModel.socialLinksList[1].getUrl().isNotEmpty()
                            && !viewModel.socialLinksList[1].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = { viewModel.showSocialLinkDialog.value = false },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = viewModel.socialLinksList.shouldEnable()
                ) {
                    Text(text = "Save Link", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InputOtherLinkDialog(viewModel: ClubDetailsScreenViewModel) {
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

                if (viewModel.otherLinksList.isEmpty()) {
                    viewModel.otherLinksList.add(LinkComposeModel())
                }

                LazyColumn(
                    state = listState, modifier = Modifier
                        .weight(1f, false)
                        .animateContentSize()
                ) {
                    items(viewModel.otherLinksList.size) { idx ->
                        OtherLinkItem(
                            modifier = Modifier.animateItemPlacement(),
                            idx,
                            viewModel.otherLinksList,
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
                            viewModel.otherLinksList.add(LinkComposeModel())
                            scope.launch { listState.animateScrollToItem(viewModel.otherLinksList.size - 1) }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorScheme.primary,
                            contentColor = contentColorFor(backgroundColor = colorScheme.primary)
                        )
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    }

                    Button(
                        onClick = { viewModel.showOtherLinkDialog.value = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = viewModel.otherLinksList.shouldEnable()
                    ) {
                        Text(text = "Save Link", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ClubProfilePic(clubModel: ClubDetailModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
//            loading.value = true
//            updateProfilePicture(context, result.uriContent!!, appViewModel, loading)
        } else {
            val exception = result.error
        }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        cropOptions.setAspectRatio(1, 1)
        imageCropLauncher.launch(cropOptions)
    }

    Row(modifier = modifier) {
        ProfilePicture(modifier = Modifier.padding(start = 46.dp), url = clubModel.avatar, size = 156.dp)

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

@Composable
fun ClubInfo(clubModel: ClubDetailModel, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Text(clubModel.name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "${clubModel.subscribers} Members",
            fontSize = 12.sp
        )
        Text(clubModel.description, fontSize = 14.sp)
    }
}

//@Composable
//fun ClubDetailsScreen(appViewModel: AppViewModel, viewModel: ClubDetailsScreenViewModel = hiltViewModel()) {
//    viewModel.initialClubModel.value = appViewModel.clubModel.value
//    viewModel.description.value = appViewModel.clubModel.value.description
//    viewModel.avatar_url.value = appViewModel.clubModel.value.avatar
//    viewModel.socialMediaUrls = appViewModel.clubModel.value.socialUrls.toMutableList()
//    viewModel.faceBookUrl.value = appViewModel.clubModel.value.socialUrls[0]
//    viewModel.instagramUrl.value = appViewModel.clubModel.value.socialUrls[1]
//    viewModel.linkedInUrl.value = appViewModel.clubModel.value.socialUrls[2]
//    viewModel.websiteUrl.value = appViewModel.clubModel.value.socialUrls[3]
//    viewModel.githubUrl.value = appViewModel.clubModel.value.socialUrls[4]
//    val isAdmin = appViewModel.clubModel.value.admins.contains(appViewModel.email.value)
//    val context = LocalContext.current
//
//    MotiClubsTheme(colorScheme = getColorScheme()) {
//        SetNavBarsTheme()
//
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Scaffold(
//                modifier = Modifier,
//                floatingActionButton = {
//                    if (isAdmin)
//                        ExtendedFloatingActionButton(
//                            text = { Text(text = "Edit", fontSize = 15.sp, textAlign = TextAlign.Center) },
//                            icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = "") },
//                            onClick = {
//                                updateClubModel(
//                                    appViewModel, viewModel,
//                                    ClubDTO(
//                                        viewModel.description.value,
//                                        viewModel.avatar_url.value,
//                                        viewModel.socialMediaUrls
//                                    ), context
//                                )
//                            },
//                            expanded = !viewModel.isEditButtonEnabled,
//                            shape = RoundedCornerShape(24.dp),
//                        )
//                },
//                floatingActionButtonPosition = FabPosition.Center
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(it),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    if (viewModel.isLoading.value) {
//                        ProgressDialog(progressMsg = "Uploading...")
//                    }
//                    ClubProfilePic(
//                        viewModel = viewModel,
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        isAdmin = isAdmin
//                    )
//                    ClubInfo(
//                        viewModel = viewModel,
//                        appViewModel = appViewModel,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 36.dp),
//                        context = context,
//                        isAdmin = isAdmin
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ClubProfilePic(viewModel: ClubDetailsScreenViewModel, modifier: Modifier = Modifier, isAdmin: Boolean) {
//    val context = LocalContext.current
//
//    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            viewModel.isLoading.value = true
//            val storageRef = Firebase.storage.reference
//            val profilePicRef =
//                storageRef.child("club_profile_images").child(viewModel.initialClubModel.value.id)
//                    .child(viewModel.initialClubModel.value.id)
//
//            val bitmap = compressBitmap(result.uriContent!!, context)
//
//            val boas = ByteArrayOutputStream()
//            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, boas)
//            profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    viewModel.isLoading.value = false
//                }
//                profilePicRef.downloadUrl
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    viewModel.avatar_url.value = task.result.toString()
//                    viewModel.isLoading.value = false
//                } else {
//                    Toast.makeText(context, "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    viewModel.isLoading.value = false
//                }
//            }
//        } else {
//            val exception = result.error
//        }
//    }
//
//    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
//        cropOptions.setAspectRatio(1, 1)
//        imageCropLauncher.launch(cropOptions)
//    }
//
//    Row(modifier = modifier) {
//        Image(
//            painter = if (viewModel.avatar_url.value.isEmpty() || !viewModel.avatar_url.value.matches(
//                    Patterns.WEB_URL.toRegex()
//                )
//            ) {
//                rememberVectorPainter(image = Icons.Outlined.AccountCircle)
//            } else {
//                rememberAsyncImagePainter(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(viewModel.avatar_url.value)
//                        .diskCachePolicy(CachePolicy.ENABLED)
//                        .diskCacheKey(Constants.AVATAR)
//                        .placeholder(R.drawable.outline_account_circle_24)
//                        .build()
//                )
//            }, contentDescription = "",
//            modifier = modifier
//                .padding(start = 46.dp)
//                .clip(CircleShape)
//                .size(156.dp)
//        )
//        if (isAdmin)
//            IconButton(
//                onClick = {
//                    launcher.launch("image/*")
//                },
//                modifier = Modifier
//                    .align(Alignment.Bottom)
//                    .border(1.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//            ) {
//                Icon(painter = rememberVectorPainter(image = Icons.Rounded.AddAPhoto), contentDescription = "")
//            }
//    }
//}
//
//@Composable
//fun ClubInfo(viewModel: ClubDetailsScreenViewModel,appViewModel: AppViewModel, modifier: Modifier = Modifier, context: Context, isAdmin: Boolean) {
//    if (viewModel.showLinkDialog.value) {
//        InputSocialLinkDialog(viewModel = viewModel)
//    }
//
//    val scrollState = rememberScrollState()
//    Column(modifier = modifier.verticalScroll(scrollState)) {
//
//        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
//            if (viewModel.socialMediaUrls[0].isNotEmpty())
//                IconButton(
//                    onClick = {
//                        val urlIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(viewModel.socialMediaUrls[0])
//                        )
//                        context.startActivity(urlIntent)
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook),
//                        contentDescription = "",
//                        tint = Color.Blue
//                    )
//                }
//
//            if (viewModel.socialMediaUrls[1].isNotEmpty())
//                IconButton(
//                    onClick = {
//                        val urlIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(viewModel.socialMediaUrls[1])
//                        )
//                        context.startActivity(urlIntent)
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Chat),
//                        contentDescription = "",
//                        tint = Color.Red
//                    )
//                }
//
//            if (viewModel.socialMediaUrls[2].isNotEmpty())
//                IconButton(
//                    onClick = {
//                        val urlIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(viewModel.socialMediaUrls[2])
//                        )
//                        context.startActivity(urlIntent)
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Webhook), contentDescription = ""
//                    )
//                }
//
//            if (viewModel.socialMediaUrls[3].isNotEmpty())
//                IconButton(
//                    onClick = {
//                        val urlIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(viewModel.socialMediaUrls[3])
//                        )
//                        context.startActivity(urlIntent)
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook),
//                        contentDescription = "",
//                        tint = getColorScheme().primary
//                    )
//                }
//
//            if (viewModel.socialMediaUrls[4].isNotEmpty())
//                IconButton(
//                    onClick = {
//                        val urlIntent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(viewModel.socialMediaUrls[4])
//                        )
//                        context.startActivity(urlIntent)
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Facebook), contentDescription = ""
//                    )
//                }
//
//            if (isAdmin)
//                IconButton(
//                    onClick = {
//                        viewModel.showLinkDialog.value = true
//                    },
//                    modifier = Modifier
//                        .align(Alignment.Bottom)
//                        .border(0.dp, getColorScheme().onSurface, shape = RoundedCornerShape(24.dp))
//                ) {
//                    Icon(
//                        painter = rememberVectorPainter(image = Icons.Rounded.Add), contentDescription = ""
//                    )
//                }
//
//        }
//
//        Text(viewModel.initialClubModel.value.name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
//
//        Text("Members: " + appViewModel.subscriberCount.value, fontSize = 15.sp, fontWeight = FontWeight.Normal)
//
//        OutlinedTextField(
//            modifier = Modifier.fillMaxWidth().padding(top=15.dp),
//            value = viewModel.description.value,
//            onValueChange = { viewModel.description.value = it },
//            shape = RoundedCornerShape(24.dp),
//            label = { Text(text = "Description", fontSize = 14.sp) },
//            enabled = isAdmin,
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                disabledTextColor = contentColorFor(backgroundColor = colorScheme.background),
//                disabledLabelColor = contentColorFor(backgroundColor = colorScheme.background),
//                disabledLeadingIconColor = contentColorFor(backgroundColor = colorScheme.background)
//            )
//        )
//
//    }
//}
//
//@Composable
//fun InputSocialLinkDialog(viewModel: ClubDetailsScreenViewModel) {
//    val colorScheme = getColorScheme()
//    Dialog(onDismissRequest = { viewModel.showLinkDialog.value = false }, DialogProperties()) {
//        Box(
//            modifier = Modifier
//                .padding(16.dp)
//                .clip(RoundedCornerShape(24.dp))
//                .background(colorScheme.background)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    "Social-Media URLs",
//                    fontSize = 16.sp,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .align(Alignment.CenterHorizontally),
//                    fontWeight = FontWeight.SemiBold
//                )
//
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = viewModel.faceBookUrl.value,
//                    onValueChange = { viewModel.faceBookUrl.value = it },
//                    shape = RoundedCornerShape(24.dp),
//                    label = { Text(text = "Facebook URL") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                )
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = viewModel.instagramUrl.value,
//                    onValueChange = { viewModel.instagramUrl.value = it },
//                    shape = RoundedCornerShape(24.dp),
//                    label = { Text(text = "Instagram URL") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                )
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = viewModel.linkedInUrl.value,
//                    onValueChange = { viewModel.linkedInUrl.value = it },
//                    shape = RoundedCornerShape(24.dp),
//                    label = { Text(text = "LinkedIn URL") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                )
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = viewModel.websiteUrl.value,
//                    onValueChange = { viewModel.websiteUrl.value = it },
//                    shape = RoundedCornerShape(24.dp),
//                    label = { Text(text = "Website URL") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                )
//
//                OutlinedTextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = viewModel.githubUrl.value,
//                    onValueChange = { viewModel.githubUrl.value = it },
//                    shape = RoundedCornerShape(24.dp),
//                    label = { Text(text = "Github URL") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                )
//
//                Button(
//                    onClick = {
//                        viewModel.socialMediaUrls = mutableListOf(
//                            viewModel.faceBookUrl.value,
//                            viewModel.instagramUrl.value,
//                            viewModel.linkedInUrl.value,
//                            viewModel.websiteUrl.value,
//                            viewModel.githubUrl.value
//                        )
//                        if (viewModel.initialClubModel.value.socialUrls.toString() != viewModel.socialMediaUrls.toString()) {
//                            viewModel.socialMediaUrlUpdated.value = true
//                        }
//                        viewModel.showLinkDialog.value = false
//                    },
//                    enabled = true
//                ) {
//                    Text(text = "Add Links", fontSize = 14.sp)
//                }
//            }
//        }
//    }
//}
//
//fun updateClubModel(
//    appViewModel: AppViewModel,
//    viewModel: ClubDetailsScreenViewModel,
//    clubDTO: ClubDTO,
//    context: Context
//) {
//    viewModel.updateClub(context.getAuthToken(), appViewModel.clubModel.value.id, clubDTO = clubDTO, {
//        viewModel.socialMediaUrlUpdated.value = false
//        viewModel.isEditButtonEnabled = false
//        viewModel.isLoading.value = false
//        appViewModel.clubModel.value.socialUrls = clubDTO.socialUrls
//        appViewModel.clubModel.value.description = clubDTO.description
//        appViewModel.clubModel.value.avatar = clubDTO.avatar
//        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
//    }) { Toast.makeText(context, "$it: Error updating club", Toast.LENGTH_SHORT).show() }
//
//}
