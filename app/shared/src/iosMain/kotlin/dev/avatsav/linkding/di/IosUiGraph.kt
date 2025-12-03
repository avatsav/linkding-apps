package dev.avatsav.linkding.di

import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.ui.MainUIViewControllerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(UiScope::class)
interface IosUiGraph {

  val uiViewControllerFactory: MainUIViewControllerFactory

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createUiComponent(): IosUiGraph
  }
}
