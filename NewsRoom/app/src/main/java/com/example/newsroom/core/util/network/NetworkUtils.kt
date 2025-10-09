package com.example.newsroom.core.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

data class NetworkStatus(
    val wifi: Boolean,
    val mobile: Boolean,
    val ethernet: Boolean,
    val internet: Boolean
)

suspend fun getNetworkStatus(context: Context): NetworkStatus = withContext(Dispatchers.IO) {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(network)

    val wifiOn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    val mobileOn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    val ethernetOn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true

    // Quick check for real internet access
    val internetOn = try {
        val socket = Socket()
        socket.connect(InetSocketAddress("8.8.8.8", 53), 1500) // Google DNS
        socket.close()
        true
    } catch (e: Exception) {
        false
    }

    NetworkStatus(
        wifi = wifiOn,
        mobile = mobileOn,
        ethernet = ethernetOn,
        internet = internetOn
    )
}

/** Simple shortcut if you only want a boolean like previous hasNetwork() */
suspend fun hasNetwork(context: Context): Boolean {
    val status = getNetworkStatus(context)
    return status.wifi || status.mobile || status.ethernet
}

fun hasNetworkSync(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}
