package com.mnnit.moticlubs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnnit.moticlubs.ui.components.CollapsibleTopAppBar
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.components.ProgressDialog
import com.mnnit.moticlubs.ui.components.addmemberscreen.CourseSelectField
import com.mnnit.moticlubs.ui.components.addmemberscreen.SearchField
import com.mnnit.moticlubs.ui.components.addmemberscreen.SelectedMemberDialog
import com.mnnit.moticlubs.ui.theme.MotiClubsTheme
import com.mnnit.moticlubs.ui.theme.SetTransparentSystemBars
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AddMemberViewModel

@Composable
fun AddMemberScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddMemberViewModel = hiltViewModel(),
) {
    val colorScheme = getColorScheme()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    MotiClubsTheme(colorScheme) {
        if (scrollBehavior.state.collapsedFraction > 0.6f) {
            SetTransparentSystemBars(setStatusBar = false)
        } else {
            SetTransparentSystemBars()
        }

        Surface(
            color = colorScheme.background,
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
            shadowElevation = 2.dp,
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    CollapsibleTopAppBar(
                        modifier = Modifier,
                        maxHeight = 386.dp,
                        bigTitle = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = viewModel.channelModel.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(end = 16.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Add Members",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier,
                                )
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding()
                                            .weight(1f),
                                    ) {
                                        Text(text = "Enter comma separated values", fontSize = 12.sp)
                                        SearchField(
                                            modifier = Modifier.fillMaxWidth(),
                                            searchText = viewModel.searchName,
                                            enabled = !viewModel.isFetching,
                                            label = "Name",
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Text,
                                                capitalization = KeyboardCapitalization.Words,
                                            ),
                                            onValueChange = { viewModel.filterSearch() },
                                        )
                                        SearchField(
                                            modifier = Modifier.fillMaxWidth(),
                                            searchText = viewModel.searchRegNo,
                                            enabled = !viewModel.isFetching,
                                            label = "RegNo",
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Text,
                                                capitalization = KeyboardCapitalization.Characters,
                                            ),
                                            onValueChange = { viewModel.filterSearch() },
                                        )
                                    }

                                    Spacer(modifier = Modifier.padding(8.dp))

                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 16.dp)
                                            .height(56.dp)
                                            .weight(0.3f),
                                        onClick = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        },
                                        colors = CardDefaults.cardColors(colorScheme.primary),
                                        shape = RoundedCornerShape(24.dp),
                                        enabled = !viewModel.isFetching,
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .fillMaxHeight(),
                                        ) {
                                            Icon(
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                imageVector = Icons.Outlined.Search,
                                                contentDescription = "",
                                                tint = colorScheme.onPrimary,
                                            )
                                        }
                                    }
                                }

                                CourseSelectField(viewModel)

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    AssistChip(
                                        onClick = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            viewModel.resetSearchFields()
                                            viewModel.filterSearch()
                                        },
                                        label = {
                                            Text(
                                                text = "Clear all",
                                                fontSize = 14.sp,
                                                color = colorScheme.primary,
                                            )
                                        },
                                        modifier = Modifier.imePadding(),
                                        shape = RoundedCornerShape(24.dp),
                                        border = AssistChipDefaults.assistChipBorder(
                                            borderColor = colorScheme.background,
                                        ),
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    AssistChip(
                                        onClick = {
                                            viewModel.searchUserList.value.forEach { user ->
                                                viewModel.selectedUserMap.value[user.userId] = user
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = "Select all",
                                                fontSize = 14.sp,
                                                color = if (viewModel.searchUserList.value.size > 0) {
                                                    contentColorFor(backgroundColor = colorScheme.primary)
                                                } else {
                                                    colorScheme.onSurfaceVariant
                                                },
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(end = 16.dp)
                                            .align(Alignment.CenterVertically)
                                            .imePadding(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = colorScheme.primary,
                                        ),
                                        border = AssistChipDefaults.assistChipBorder(borderColor = colorScheme.primary),
                                        enabled = viewModel.searchUserList.value.size > 0,
                                    )
                                }

                                AnimatedVisibility(
                                    visible = viewModel.isFetching,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    LinearProgressIndicator(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        strokeCap = StrokeCap.Round,
                                    )
                                }
                            }
                        },
                        smallTitle = {
                            Text(text = "Search Results")
                        },
                        navigationIcon = {
                            IconButton(
                                modifier = Modifier.size(42.dp),
                                onClick = onBackPressed,
                            ) {
                                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                            }
                        },
                        actions = {
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
            ) { pd ->

                if (viewModel.showProgressDialog) {
                    ProgressDialog(progressMsg = "Adding members")
                }

                if (viewModel.showSelectedMemberDialog) {
                    SelectedMemberDialog(
                        viewModel = viewModel,
                        onAdd = {
                            viewModel.showSelectedMemberDialog = false
                            viewModel.showProgressDialog = true
                            viewModel.addMembers(onBackPressed)
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(PaddingValues(top = pd.calculateTopPadding()))
                        .imePadding()
                        .fillMaxSize()
                        .animateContentSize(),
                ) {
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp),
                        visible = viewModel.selectedUserMap.value.size > 0,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        FilterChip(
                            onClick = {
                                viewModel.showSelectedMemberDialog = true
                            },
                            label = {
                                Text(
                                    text = "${viewModel.selectedUserMap.value.size} selected",
                                    fontSize = 14.sp,
                                    color = colorScheme.primary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    modifier = Modifier.padding(2.dp),
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "",
                                    tint = colorScheme.primary,
                                )
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally)
                                .imePadding(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = FilterChipDefaults.filterChipElevation(8.dp),
                            selected = true,
                        )
                    }

                    UserList(viewModel)
                }
            }
        }
    }
}

@Composable
private fun UserList(
    viewModel: AddMemberViewModel,
) {
    val colorScheme = getColorScheme()
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.animateContentSize(),
        state = scrollState,
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 72.dp),
    ) {
        item {
            if (viewModel.searchUserList.value.isEmpty()) {
                Text(
                    text = "No results",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }

        items(viewModel.searchUserList.value.size) { index ->
            Card(
                modifier = Modifier.padding(top = 8.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    colorScheme.surfaceColorAtElevation(
                        if (viewModel.selectedUserMap.value.containsKey(viewModel.searchUserList.value[index].userId)) {
                            16.dp
                        } else {
                            2.dp
                        },
                    ),
                ),
                border = BorderStroke(
                    width = if (viewModel.selectedUserMap.value.containsKey(
                            viewModel.searchUserList.value[index].userId,
                        )
                    ) {
                        1.dp
                    } else {
                        0.dp
                    },
                    color = if (viewModel.selectedUserMap.value.containsKey(
                            viewModel.searchUserList.value[index].userId,
                        )
                    ) {
                        colorScheme.primary
                    } else {
                        colorScheme.background
                    },
                ),
                onClick = {
                    if (viewModel.selectedUserMap.value.containsKey(viewModel.searchUserList.value[index].userId)) {
                        viewModel.selectedUserMap.value.remove(viewModel.searchUserList.value[index].userId)
                    } else {
                        viewModel.selectedUserMap.value[viewModel.searchUserList.value[index].userId] =
                            viewModel.searchUserList.value[index]
                    }
                },
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                ) {
                    ProfilePicture(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        userModel = viewModel.searchUserList.value[index],
                        size = 48.dp,
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text(
                            modifier = Modifier,
                            text = viewModel.searchUserList.value[index].name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            modifier = Modifier,
                            text = viewModel.searchUserList.value[index].regNo,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
