package com.mnnit.moticlubs.domain.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

fun Context.connectionAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nwCap = connectivityManager.activeNetwork
    val activeNw = connectivityManager.getNetworkCapabilities(nwCap) ?: return false
    return when {
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

class NetworkListener(context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val statusFlow = callbackFlow {
        var status = if (context.connectionAvailable()) NetworkStatus.Connected else NetworkStatus.Disconnected

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                Log.d("TAG", "onUnavailable: ")
                status = NetworkStatus.Disconnected
                trySend(status)
            }

            override fun onAvailable(network: Network) {
                Log.d("TAG", "onAvailable: ")
                status = NetworkStatus.Connected
                trySend(status)
            }

            override fun onLost(network: Network) {
                Log.d("TAG", "onLost: ")
                status = NetworkStatus.Disconnected
                trySend(status)
            }
        }
        trySend(status)

        val req = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(req, networkCallback)
        awaitClose {
            cm.unregisterNetworkCallback(networkCallback)
        }
    }
}

inline fun <Result> Flow<NetworkStatus>.map(
    crossinline onUnavailable: suspend () -> Result,
    crossinline onAvailable: suspend () -> Result,
): Flow<Result> = map { status ->
    when (status) {
        NetworkStatus.Disconnected -> onUnavailable()
        NetworkStatus.Connected -> onAvailable()
    }
}

sealed class NetworkStatus {
    object Disconnected : NetworkStatus()
    object Connected : NetworkStatus()
}
