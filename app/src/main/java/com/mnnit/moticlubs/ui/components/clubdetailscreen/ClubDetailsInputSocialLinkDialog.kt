package com.mnnit.moticlubs.ui.components.clubdetailscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.util.OtherLinkComposeModel
import com.mnnit.moticlubs.domain.util.PublishedList
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.domain.util.SocialLinkComposeModel
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun InputSocialLinkDialog(
    showDialog: PublishedState<Boolean>,
    socialLinksLiveList: PublishedList<SocialLinkComposeModel>,
    otherLinksLiveList: PublishedList<OtherLinkComposeModel>,
    onClick: (list: List<UrlModel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()
    Dialog(onDismissRequest = { showDialog.value = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.85f)
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
                    value = socialLinksLiveList.value[0].urlFieldValue.value,
                    onValueChange = { socialLinksLiveList.value[0].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Facebook") },
                    singleLine = true,
                    isError = !socialLinksLiveList.value[0].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = socialLinksLiveList.value[1].urlFieldValue.value,
                    onValueChange = { socialLinksLiveList.value[1].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Instagram") },
                    singleLine = true,
                    isError = !socialLinksLiveList.value[1].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = socialLinksLiveList.value[2].urlFieldValue.value,
                    onValueChange = { socialLinksLiveList.value[2].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Twitter") },
                    singleLine = true,
                    isError = !socialLinksLiveList.value[2].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = socialLinksLiveList.value[3].urlFieldValue.value,
                    onValueChange = { socialLinksLiveList.value[3].urlFieldValue.value = it },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(text = "Github") },
                    singleLine = true,
                    isError = !socialLinksLiveList.value[3].validUrl(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )

                Button(
                    onClick = {
                        val list = socialLinksLiveList.value
                            .filter { it.validUrl() }.map { it.mapToUrlModel() }
                            .toMutableList()
                        val others = otherLinksLiveList.value
                            .filter { it.validUrl() && it.getName().isTrimmedNotEmpty() }.map { it.mapToUrlModel() }
                        list.addAll(others)
                        onClick(list)
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = socialLinksLiveList.value.any { it.validUrl() }
                ) {
                    Text(text = "Save Link(s)", fontSize = 14.sp)
                }
            }
        }
    }
}
