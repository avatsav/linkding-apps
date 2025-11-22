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

@Serializable data object AuthScreen : NavKey

@Serializable data object BookmarksScreen : NavKey

@Serializable data class AddBookmarkScreen(val sharedUrl: String? = null) : NavKey

@Serializable data class UrlScreen(val url: String) : NavKey

@Serializable data object SettingsScreen : NavKey

@Serializable data object TagsScreen : NavKey

@ContributesTo(AppScope::class)
interface NavigationComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(NavKey::class) {
        subclass(AuthScreen::class, AuthScreen.serializer())
        subclass(BookmarksScreen::class, BookmarksScreen.serializer())
        subclass(AddBookmarkScreen::class, AddBookmarkScreen.serializer())
        subclass(UrlScreen::class, UrlScreen.serializer())
        subclass(SettingsScreen::class, SettingsScreen.serializer())
        subclass(TagsScreen::class, TagsScreen.serializer())
      }
    }
  }
}
