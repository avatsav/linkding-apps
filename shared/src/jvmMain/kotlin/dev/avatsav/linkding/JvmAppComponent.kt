package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Component

@Component
@AppScope
abstract class JvmAppComponent : SharedAppComponent {
    companion object
}
