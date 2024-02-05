package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Component

@Component
@AppScope
abstract class IosAppComponent : SharedAppComponent {
    companion object
}
