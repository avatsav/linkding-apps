package dev.avatsav.linkding

import dev.avatsav.linkding.inject.UiScope
import me.tatarka.inject.annotations.Component

@Component
@UiScope
abstract class JvmUiComponent(
    @Component val appComponent: JvmAppComponent,
) : SharedUiComponent {
    companion object
}
