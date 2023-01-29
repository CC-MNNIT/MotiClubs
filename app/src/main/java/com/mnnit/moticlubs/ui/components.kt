package com.mnnit.moticlubs.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.mnnit.moticlubs.R
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