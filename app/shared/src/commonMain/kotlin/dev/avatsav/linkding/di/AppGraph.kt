package dev.avatsav.linkding.di

import dev.avatsav.linkding.initializers.AppInitializer
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppGraph : ViewModelGraph {
  val appInitializer: AppInitializer
}
