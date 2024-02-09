package dev.avatsav.linkding

import android.app.Activity
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.root.AppContent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@UiScope
abstract class AndroidActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: AndroidAppComponent,
) : SharedUiComponent {

    abstract val appContent: AppContent

    companion object
}
