package dev.avatsav.linkding.internet.inject

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.internet.EmptyNetworkMonitor
import dev.avatsav.linkding.internet.NetworkMonitor
import me.tatarka.inject.annotations.Provides

actual interface PlatformNetworkMonitorComponent {
    @Provides
    @AppScope
    fun provideNetworkMonitor(): NetworkMonitor = EmptyNetworkMonitor()
}
