package dev.avatsav.linkding.initializers

import dev.avatsav.linkding.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class AppInitializer(private val initializers: Set<Initializer>) : Initializer {
  override fun initialize() {
    initializers.forEach { it.initialize() }
  }
}
