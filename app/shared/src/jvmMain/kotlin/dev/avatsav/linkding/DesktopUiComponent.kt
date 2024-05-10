package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.root.AppUi
import me.tatarka.inject.annotations.Component

@Component
@UiScope
abstract class DesktopUiComponent(
    @Component val appComponent: DesktopAppComponent,
) : SharedUiComponent {

    abstract val appUi: AppUi

    companion object
}
