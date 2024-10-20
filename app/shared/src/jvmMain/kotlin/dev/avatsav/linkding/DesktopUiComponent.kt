package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.ContributesSubcomponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.ui.AppUi

@SingleIn(UiScope::class)
@ContributesSubcomponent(
    scope = UiScope::class,
    parentScope = AppScope::class,
)
interface DesktopUiComponent {

    val appUi: AppUi

    @ContributesSubcomponent.Factory
    interface Factory {
        fun create(): DesktopUiComponent
    }
}
