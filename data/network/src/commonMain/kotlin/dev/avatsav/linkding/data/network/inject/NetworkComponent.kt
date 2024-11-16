package dev.avatsav.linkding.data.network.inject

import dev.avatsav.linkding.inject.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

expect interface PlatformNetworkComponent

@ContributesTo(AppScope::class)
interface NetworkComponent : PlatformNetworkComponent
