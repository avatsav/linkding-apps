package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.ContributesSubcomponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.ui.MainUIViewController
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController

@SingleIn(UiScope::class)
@ContributesSubcomponent(
    scope = UiScope::class,
    parentScope = AppScope::class,
)
interface IosUiComponent {

    val uiViewControllerFactory: () -> UIViewController

    @Provides
    @SingleIn(UiScope::class)
    fun uiViewController(impl: MainUIViewController): UIViewController = impl()

    @ContributesSubcomponent.Factory
    interface Factory {
        fun create(): IosUiComponent
    }
}
