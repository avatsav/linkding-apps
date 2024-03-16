package dev.avatsav.linkding.internet.inject

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.internet.DefaultConnectivityObserver
import me.tatarka.inject.annotations.Provides

expect interface PlatformNetworkMonitorComponent

interface ConnectivityComponent : PlatformNetworkMonitorComponent {

    @AppScope
    @Provides
    fun provideInternetConnectivityObserver(bind: DefaultConnectivityObserver): ConnectivityObserver =
        bind
}
