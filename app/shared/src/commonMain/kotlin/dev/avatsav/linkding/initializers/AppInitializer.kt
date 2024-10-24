package dev.avatsav.linkding.initializers

import dev.avatsav.linkding.Initializer
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Inject

@Inject
@SingleIn(AppScope::class)
class AppInitializer(private val initializers: Set<Initializer>) : Initializer {
    override fun initialize() {
        initializers.forEach {
            it.initialize()
        }
    }
}
