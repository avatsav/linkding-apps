package dev.avatsav.linkding.internet

interface NetworkMonitor {
    val isOnline: Boolean
    fun close()
    fun setListener(listener: Listener)
    fun interface Listener {
        fun onConnectivityChange(isOnline: Boolean)
    }
}
