package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent

@MergeComponent(AppScope::class)
abstract class IosAppComponent : IosAppComponentMerged {
    companion object
}
