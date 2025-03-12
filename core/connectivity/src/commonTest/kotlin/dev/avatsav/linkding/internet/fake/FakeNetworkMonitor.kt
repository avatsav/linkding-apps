package dev.avatsav.linkding.internet.fake

import dev.avatsav.linkding.internet.NetworkMonitor

class FakeNetworkMonitor : NetworkMonitor {
  private var listener: NetworkMonitor.Listener? = null
  private var _isOnline = true

  private var _closed = false

  val closed: Boolean
    get() = _closed

  override val isOnline: Boolean
    get() = _isOnline

  override fun close() {
    _closed = true
  }

  override fun setListener(listener: NetworkMonitor.Listener) {
    this.listener = listener
  }

  fun setConnectivityChanged(isOnline: Boolean) {
    _isOnline = isOnline
    listener?.onConnectivityChange(isOnline)
  }
}
