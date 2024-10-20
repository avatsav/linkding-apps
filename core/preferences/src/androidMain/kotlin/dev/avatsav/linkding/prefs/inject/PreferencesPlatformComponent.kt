package dev.avatsav.linkding.prefs.inject

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

actual interface PreferencesPlatformComponent {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(sharedPrefs: SharedPreferences): ObservableSettings =
        SharedPreferencesSettings(sharedPrefs)

    @Provides
    @SingleIn(AppScope::class)
    fun provideSharedPreferences(
        context: Application,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}
