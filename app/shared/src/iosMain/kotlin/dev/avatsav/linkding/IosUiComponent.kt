package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.ui.MainUIViewController
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent

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
