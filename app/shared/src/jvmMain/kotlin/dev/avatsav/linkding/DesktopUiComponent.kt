package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.AppUi
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(UiScope::class)
@ContributesSubcomponent(UiScope::class)
interface DesktopUiComponent {

    val appUi: AppUi

    @ContributesSubcomponent.Factory(AppScope::class)
    interface Factory {
        fun create(): DesktopUiComponent
    }
}
