package dev.avatsav.linkding.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

sealed interface Screen : NavKey {
  @Serializable data object Auth : Screen

  @Serializable data object BookmarksFeed : Screen

  @Serializable data class AddBookmark(val sharedUrl: String? = null) : Screen

  @Serializable data class Url(val url: String) : Screen

  @Serializable data object Settings : Screen

  @Serializable data object Tags : Screen
}

@ContributesTo(AppScope::class)
interface NavigationComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(NavKey::class) {
        subclass(Screen.Auth::class, Screen.Auth.serializer())
        subclass(Screen.BookmarksFeed::class, Screen.BookmarksFeed.serializer())
        subclass(Screen.AddBookmark::class, Screen.AddBookmark.serializer())
        subclass(Screen.Url::class, Screen.Url.serializer())
        subclass(Screen.Settings::class, Screen.Settings.serializer())
        subclass(Screen.Tags::class, Screen.Tags.serializer())
      }
    }
  }
}
