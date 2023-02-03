package com.mnnit.moticlubs.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.R
import com.squareup.picasso3.NetworkPolicy
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.compose.rememberPainter

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    url: String,
    size: Dp = 48.dp,
    onClick: (() -> Unit)? = null
) {
    Image(
        painter = LocalContext.current.getImageUrlPainter(url = url), contentDescription = "",
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .clickable(enabled = onClick != null, onClick = { onClick?.let { onClick() } })
    )
}

@Composable
private fun Context.getImageUrlPainter(url: String): Painter {
    if (url.isEmpty()) {
        return painterResource(id = R.drawable.outline_account_circle_24)
    }

    val picasso = remember { mutableStateOf(Picasso.Builder(this).build()) }
    val error = remember { mutableStateOf(false) }
    return if (error.value) {
        picasso.value.rememberPainter(request = {
            it.load(url).placeholder(R.drawable.outline_account_circle_24).error(R.drawable.outline_account_circle_24)
        }, key = url, onError = { Log.d("TAG", "getImageUrlPainter: network error") })
    } else {
        picasso.value.rememberPainter(request = {
            it.load(url).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.outline_account_circle_24).error(R.drawable.outline_account_circle_24)
        }, key = url, onError = {
            Log.d("TAG", "getImageUrlPainter: Error, fallback to network")
            error.value = true
        })
    }
}
