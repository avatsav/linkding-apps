package dev.avatsav.linkding.prefs.inject

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

actual interface PreferencesPlatformComponent {
    @Provides
    @AppScope
    fun provideSettings(sharedPrefs: SharedPreferences): ObservableSettings {
        return SharedPreferencesSettings(sharedPrefs)
    }

    @Provides
    @AppScope
    fun provideSharedPreferences(
        context: Application,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}
