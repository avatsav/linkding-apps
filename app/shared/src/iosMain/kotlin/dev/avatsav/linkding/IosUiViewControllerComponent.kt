package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.MainUIViewController
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController

@Component
@UiScope
abstract class IosUiViewControllerComponent(
    @Component val appComponent: IosAppComponent,
) : SharedUiComponent {

    abstract val uiViewControllerFactory: () -> UIViewController

    @Provides
    @UiScope
    fun userComponentFactory(): UserComponentFactory = object : UserComponentFactory {
        override fun create(apiConfig: ApiConfig): SharedUserComponent {
            TODO("Not yet implemented")
        }

    }

    @Provides
    @UiScope
    fun uiViewController(impl: MainUIViewController): UIViewController = impl()

    companion object
}
