package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.ui.components.profilescreen.UserInfo
import com.mnnit.moticlubs.ui.theme.colorScheme

@Composable
fun ProfileDialog(
    userModel: User,
    showDialog: PublishedState<Boolean>,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { showDialog.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    "Profile",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                )

                ProfilePicture(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    userModel = userModel,
                    size = 156.dp,
                    onClick = {},
                )
                UserInfo(userModel = userModel, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
