package dev.avatsav.linkding.inject

import android.app.Activity
import dev.avatsav.linkding.ui.AppUi
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(UiScope::class)
@ContributesSubcomponent(UiScope::class)
interface AndroidUiComponent {

  val appUi: AppUi

  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun create(activity: Activity): AndroidUiComponent
  }
}
