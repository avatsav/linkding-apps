package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.AppUi
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(UiScope::class)
@ContributesSubcomponent(UiScope::class)
interface DesktopUiComponent {

    val appUi: AppUi

    @Provides
    fun uiComponent(): DesktopUiComponent = this

    @ContributesSubcomponent.Factory(AppScope::class)
    interface Factory {
        fun createUiComponent(): DesktopUiComponent
    }

    companion object
}
