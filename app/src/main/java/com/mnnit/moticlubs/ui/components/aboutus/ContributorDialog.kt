package com.mnnit.moticlubs.ui.components.aboutus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.data.network.dto.GithubContributorDto
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.ui.components.ProfilePicture
import com.mnnit.moticlubs.ui.theme.colorScheme
import com.mnnit.moticlubs.ui.viewmodel.AboutUsViewModel

@Composable
fun ContributorDialog(
    app: Boolean,
    viewModel: AboutUsViewModel,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    Dialog(
        onDismissRequest = { viewModel.showContributorDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "${if (app) "App" else "Backend"} Contributors",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                )

                AnimatedVisibility(
                    visible = viewModel.loadingContributors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        strokeCap = StrokeCap.Round,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    LazyColumn(
                        modifier = Modifier.animateContentSize(),
                        state = scrollState,
                    ) {
                        if (app) {
                            items(viewModel.appContributors.value.size) {
                                ContributorItem(viewModel.appContributors.value[it])
                            }
                        } else {
                            items(viewModel.backendContributors.value.size) {
                                ContributorItem(viewModel.backendContributors.value[it])
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributorItem(contributor: GithubContributorDto) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .safeContentPadding()
            .padding(top = 8.dp),
        onClick = {
            uriHandler.openUri(contributor.htmlUrl)
        },
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            ProfilePicture(
                modifier = Modifier.align(Alignment.CenterVertically),
                userModel = User().copy(avatar = contributor.avatar),
                size = 48.dp,
                onClick = {
                    uriHandler.openUri(contributor.htmlUrl)
                },
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier,
                    text = contributor.loginName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier,
                    text = "${contributor.contributions} contributions",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .padding()
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = "",
            )
        }
    }
}
