package com.mnnit.moticlubs.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.squareup.picasso3.NetworkPolicy
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.compose.rememberPainter

@Composable
fun ProfilePicture(
    userModel: User,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    onClick: (() -> Unit)? = null,
) {
    val showDialog = remember { publishedStateOf(false) }
    if (showDialog.value) {
        ProfileDialog(userModel = userModel, showDialog = showDialog)
    }

    Image(
        painter = LocalContext.current.getProfileUrlPainter(url = userModel.avatar),
        contentDescription = "",
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .clickable(enabled = true, onClick = {
                if (onClick != null) {
                    onClick()
                    return@clickable
                }

                showDialog.value = true
            },),
    )
}

@Composable
private fun Context.getUrlPainter(url: String, profile: Boolean): Painter {
    val resID = if (profile) R.drawable.outline_account_circle_24 else R.drawable.outline_image_24
    if (url.isEmpty()) {
        return painterResource(id = resID)
    }

    val picasso = remember { publishedStateOf(Picasso.Builder(this).build()) }
    val error = remember { publishedStateOf(false) }
    return if (error.value) {
        picasso.value.rememberPainter(request = {
            it.load(url).placeholder(resID).error(resID)
        }, key = url, onError = { Log.d("TAG", "getProfileUrlPainter: network error") },)
    } else {
        picasso.value.rememberPainter(request = {
            it.load(url).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(resID).error(resID)
        }, key = url, onError = {
                Log.d("TAG", "getProfileUrlPainter: Error, fallback to network")
                error.value = true
            },)
    }
}

@Composable
private fun Context.getProfileUrlPainter(url: String): Painter = getUrlPainter(url = url, profile = true)

@Composable
fun Context.getImageUrlPainter(url: String): Painter = getUrlPainter(url = url, profile = false)
