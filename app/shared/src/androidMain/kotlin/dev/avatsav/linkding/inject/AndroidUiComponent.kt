package dev.avatsav.linkding.inject

import dev.avatsav.linkding.ui.AppUi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(UiScope::class)
interface AndroidUiComponent {

  val appUi: AppUi

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory()
  interface Factory {
    fun create(): AndroidUiComponent
  }
}
