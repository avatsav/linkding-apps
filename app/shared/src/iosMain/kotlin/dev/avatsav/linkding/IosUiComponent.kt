package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.MainUIViewController
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController

@Component
@UiScope
abstract class IosUiComponent(
    @Component val appComponent: IosAppComponent,
) : SharedUiComponent {

    abstract val uiViewControllerFactory: () -> UIViewController

    @Provides
    @UiScope
    internal fun uiComponent(): IosUiComponent = this

    @UiScope
    internal val IosUserComponentFactory.bind: UserComponentFactory
        @Provides get() = this

    @Provides
    @UiScope
    fun uiViewController(impl: MainUIViewController): UIViewController = impl()

    companion object
}
