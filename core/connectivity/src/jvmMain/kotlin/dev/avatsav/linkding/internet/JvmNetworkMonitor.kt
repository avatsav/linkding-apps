package dev.avatsav.linkding.internet

import com.r0adkll.kimchi.annotations.ContributesBinding
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class JvmNetworkMonitor : NetworkMonitor {
    override val isOnline: Boolean
        get() = true

    override fun close() {}

    override fun setListener(listener: NetworkMonitor.Listener) {}
}
