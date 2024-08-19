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
    @UiScope
    fun userComponentFactory(): UserComponentFactory = DesktopUserComponentFactory(this)

    companion object
}
