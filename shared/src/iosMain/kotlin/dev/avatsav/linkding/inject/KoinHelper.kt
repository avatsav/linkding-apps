package dev.avatsav.linkding.inject

import org.koin.core.context.startKoin

fun initKoin(enableNetworkLogging: Boolean) {
    startKoin {
        modules(sharedModule(enableNetworkLogging))
    }
}