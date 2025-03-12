package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

actual interface PreferencesPlatformComponent {
  @Provides
  @SingleIn(AppScope::class)
  fun provideSettings(delegate: Preferences): ObservableSettings = PreferencesSettings(delegate)
}
