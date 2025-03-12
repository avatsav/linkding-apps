package dev.avatsav.linkding.internet

interface NetworkMonitor {
  val isOnline: Boolean

  fun close()

  fun setListener(listener: Listener)

  fun interface Listener {
    fun onConnectivityChange(isOnline: Boolean)
  }
}

class EmptyNetworkMonitor : NetworkMonitor {
  override val isOnline: Boolean
    get() = true

  override fun close() {}

  override fun setListener(listener: NetworkMonitor.Listener) {}
}
