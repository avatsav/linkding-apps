package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.root.AppContent
import me.tatarka.inject.annotations.Component

@Component
@UiScope
abstract class DesktopUiComponent(
    @Component val appComponent: DesktopAppComponent,
) : SharedUiComponent {

    abstract val appContent: AppContent

    companion object
}
