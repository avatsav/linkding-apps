package dev.avatsav.linkding

import android.app.Activity
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.AppUi
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@UiScope
abstract class AndroidUiComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: AndroidAppComponent,
) : SharedUiComponent {

    abstract val appUi: AppUi

    @Provides
    @UiScope
    internal fun uiComponent(): AndroidUiComponent = this

    @UiScope
    internal val AndroidUserComponentFactory.bind: UserComponentFactory
        @Provides get() = this

    companion object
}
