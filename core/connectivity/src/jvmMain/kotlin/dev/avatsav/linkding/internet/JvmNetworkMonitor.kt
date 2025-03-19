package dev.avatsav.linkding.internet

import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

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
