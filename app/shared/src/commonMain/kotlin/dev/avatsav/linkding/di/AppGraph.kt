package dev.avatsav.linkding.di

import dev.avatsav.linkding.initializers.AppInitializer

interface AppGraph {
  val appInitializer: AppInitializer
}
