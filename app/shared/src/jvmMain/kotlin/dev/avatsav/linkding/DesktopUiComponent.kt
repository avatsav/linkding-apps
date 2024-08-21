package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.AppUi
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@UiScope
abstract class DesktopUiComponent(
    @Component val appComponent: DesktopAppComponent,
) : SharedUiComponent {

    abstract val appUi: AppUi

    @Provides
    internal fun uiComponent(): DesktopUiComponent = this

    @UiScope
    internal val DesktopUserComponentFactory.bind: UserComponentFactory
        @Provides get() = this

    companion object
}
