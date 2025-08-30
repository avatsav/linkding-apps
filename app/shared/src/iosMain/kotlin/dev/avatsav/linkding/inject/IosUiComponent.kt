package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.MainUIViewController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.UIKit.UIViewController

@GraphExtension(UiScope::class)
interface IosUiComponent {

  val uiViewControllerFactory: () -> UIViewController

  @Provides
  @SingleIn(UiScope::class)
  fun uiViewController(impl: MainUIViewController): UIViewController = impl()

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createUiComponent(): IosUiComponent
  }
}
