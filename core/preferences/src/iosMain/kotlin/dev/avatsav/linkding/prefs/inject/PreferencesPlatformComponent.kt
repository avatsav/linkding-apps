package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSUserDefaults

actual interface PreferencesPlatformComponent {
  @Provides
  @SingleIn(AppScope::class)
  fun provideSettings(delegate: NSUserDefaults): ObservableSettings =
    NSUserDefaultsSettings(delegate)
}
