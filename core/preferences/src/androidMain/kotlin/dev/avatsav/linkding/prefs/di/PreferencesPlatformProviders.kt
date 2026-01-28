package dev.avatsav.linkding.prefs.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

actual interface PreferencesPlatformProviders {
  @Provides
  @SingleIn(AppScope::class)
  fun provideSettings(sharedPrefs: SharedPreferences): ObservableSettings =
    SharedPreferencesSettings(sharedPrefs)

  @Provides
  @SingleIn(AppScope::class)
  fun provideSharedPreferences(context: Application): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)
}
