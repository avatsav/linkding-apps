package dev.avatsav.linkding.initializers

import dev.avatsav.linkding.Initializer
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class AppInitializer(private val initializers: Set<Initializer>) : Initializer {
    override fun initialize() {
        initializers.forEach {
            it.initialize()
        }
    }
}
