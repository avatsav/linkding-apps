package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.MainUIViewControllerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(UiScope::class)
interface IosUiComponent {

  val uiViewControllerFactory: MainUIViewControllerFactory

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createUiComponent(): IosUiComponent
  }
}
