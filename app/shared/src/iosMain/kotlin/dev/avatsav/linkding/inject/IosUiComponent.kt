package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.MainUIViewController
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(UiScope::class)
@ContributesSubcomponent(UiScope::class)
interface IosUiComponent {

  val uiViewControllerFactory: () -> UIViewController

  @Provides
  @SingleIn(UiScope::class)
  fun uiViewController(impl: MainUIViewController): UIViewController = impl()

  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun createUiComponent(): IosUiComponent
  }
}
