package dev.avatsav.linkding

import android.app.Activity
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.ui.AppUi
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent

@SingleIn(UiScope::class)
@ContributesSubcomponent(UiScope::class)
interface AndroidUiComponent {

    val appUi: AppUi

    @ContributesSubcomponent.Factory(AppScope::class)
    interface Factory {
        fun create(activity: Activity): AndroidUiComponent
    }
}
