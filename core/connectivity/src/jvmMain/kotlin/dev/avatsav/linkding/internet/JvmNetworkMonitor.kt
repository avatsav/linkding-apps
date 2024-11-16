package dev.avatsav.linkding.internet

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class JvmNetworkMonitor : NetworkMonitor {
    override val isOnline: Boolean
        get() = true

    override fun close() {}

    override fun setListener(listener: NetworkMonitor.Listener) {}
}
