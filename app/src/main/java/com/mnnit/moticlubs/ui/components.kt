package com.mnnit.moticlubs.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.R
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.squareup.picasso3.NetworkPolicy
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.compose.rememberPainter

@Composable
fun Context.getImageUrlPainter(url: String): Painter {
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

@Composable
fun ProgressDialog(progressMsg: String) {
    val colorScheme = getColorScheme()
    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = colorScheme.primary
                )
                Text(
                    progressMsg,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
