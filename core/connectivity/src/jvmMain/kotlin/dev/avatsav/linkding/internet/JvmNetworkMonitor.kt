package dev.avatsav.linkding.internet

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class JvmNetworkMonitor : NetworkMonitor {
    override val isOnline: Boolean
        get() = true

    override fun close() {}

    override fun setListener(listener: NetworkMonitor.Listener) {}
}
