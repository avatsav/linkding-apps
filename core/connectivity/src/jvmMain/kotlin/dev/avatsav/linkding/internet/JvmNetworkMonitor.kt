package dev.avatsav.linkding.internet

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class JvmNetworkMonitor : NetworkMonitor {
  override val isOnline: Boolean
    get() = true

  override fun close() {
    // No-op
  }

  override fun setListener(listener: NetworkMonitor.Listener) {
    // No-op
  }
}
