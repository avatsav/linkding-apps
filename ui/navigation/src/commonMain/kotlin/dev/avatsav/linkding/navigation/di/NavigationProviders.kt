package dev.avatsav.linkding.navigation.di

import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.navigation.Route
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@ContributesTo(AppScope::class)
interface NavigationProviders {

  @OptIn(ExperimentalSerializationApi::class)
  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(Route::class) { subclassesOfSealed<Route>() }
    }
  }
}
