package dev.avatsav.linkding.internet

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest

@SuppressLint("MissingPermission")
class AndroidNetworkMonitor(
    private val connectivityManager: ConnectivityManager,
) : NetworkMonitor {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = onConnectivityChange(network, true)
        override fun onLost(network: Network) = onConnectivityChange(network, false)
    }

    private var listener: NetworkMonitor.Listener? = null

    override val isOnline: Boolean
        get() = connectivityManager.isOnline()

    init {
        val request = NetworkRequest.Builder().addCapability(NET_CAPABILITY_INTERNET).build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun close() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun setListener(listener: NetworkMonitor.Listener) {
        this.listener = listener
    }

    private fun onConnectivityChange(network: Network, isOnline: Boolean) {
        val isAnyOnline = connectivityManager.activeNetwork.let {
            if (it == network) {
                isOnline
            } else {
                connectivityManager.isOnline()
            }
        }
        listener?.onConnectivityChange(isAnyOnline)
    }

    private fun ConnectivityManager.isOnline() =
        activeNetwork?.let(::getNetworkCapabilities)?.hasCapability(NET_CAPABILITY_INTERNET)
            ?: true
}
