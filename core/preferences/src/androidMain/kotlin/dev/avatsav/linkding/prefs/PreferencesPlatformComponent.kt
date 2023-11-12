package dev.avatsav.linkding.prefs

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.avatsav.linkding.inject.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface PreferencesPlatformComponent {
    @Provides
    @ApplicationScope
    fun provideSettings(sharedPrefs: SharedPreferences): ObservableSettings {
        return SharedPreferencesSettings(sharedPrefs)
    }

    @Suppress("DEPRECATION")
    @Provides
    @ApplicationScope
    fun provideSharedPreferences(
        context: Application,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}
