package dev.avatsav.linkding.data.network.inject

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

expect interface PlatformNetworkComponent

@ContributesTo(AppScope::class) interface NetworkComponent : PlatformNetworkComponent
