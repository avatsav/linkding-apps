package dev.avatsav.linkding.data.network.inject

import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.inject.AppScope

expect interface PlatformNetworkComponent

@ContributesTo(AppScope::class)
interface NetworkComponent : PlatformNetworkComponent
