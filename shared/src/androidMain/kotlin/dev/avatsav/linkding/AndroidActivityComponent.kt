package dev.avatsav.linkding

import android.app.Activity
import androidx.core.os.ConfigurationCompat
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.ui.root.AppContent
import java.util.Locale
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@UiScope
abstract class AndroidActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: AndroidAppComponent,
) : SharedUiComponent {

    abstract val appContent: AppContent

    @Provides
    fun provideActivityLocale(activity: Activity): Locale {
        return ConfigurationCompat.getLocales(activity.resources.configuration).get(0)
            ?: Locale.getDefault()
    }

    companion object
}
