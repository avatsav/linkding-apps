package dev.avatsav.linkding.prefs

expect interface PreferencesPlatformComponent

const val AppPreferencesStorageKey = "app-preferences"

interface PreferencesComponent : PreferencesPlatformComponent {

//    @AppScope
//    @Provides
//    fun appPreferences(
//        settings: ObservableSettings,
//        dispatchers: AppCoroutineDispatchers,
//    ): AppPreferences {
//        return DefaultAppPreferences(settings, dispatchers)
//    }
}
