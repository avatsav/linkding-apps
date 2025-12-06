package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.data.model.Tag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@OptIn(ExperimentalUuidApi::class)
@Serializable
sealed interface Screen : NavKey {
  /** Unique key for this screen instance, used for result routing. */
  val key: String

  @Serializable data class Auth(override val key: String = Uuid.random().toString()) : Screen

  @Serializable
  data class BookmarksFeed(override val key: String = Uuid.random().toString()) : Screen

  @Serializable
  data class AddBookmark(
    val sharedUrl: String? = null,
    override val key: String = Uuid.random().toString(),
  ) : Screen

  @Serializable
  data class Url(val url: String, override val key: String = Uuid.random().toString()) : Screen

  @Serializable data class Settings(override val key: String = Uuid.random().toString()) : Screen

  /** Tag selection screen with typed results. */
  @Serializable
  data class Tags(
    val selectedTagIds: List<Long> = emptyList(),
    override val key: String = Uuid.random().toString(),
  ) : Screen, ScreenWithResult<Tags.Result> {

    sealed interface Result : NavResult {
      @Serializable @Immutable data class Confirmed(val selectedTags: List<Tag>) : Result

      @Serializable @Immutable data object Dismissed : Result
    }
  }
}

@ContributesTo(AppScope::class)
interface NavigationComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSavedStateConfiguration(): SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
      polymorphic(NavKey::class) {
        subclass(Screen.Auth::class)
        subclass(Screen.BookmarksFeed::class)
        subclass(Screen.AddBookmark::class)
        subclass(Screen.Url::class)
        subclass(Screen.Settings::class)
        subclass(Screen.Tags::class)
      }
      polymorphic(Screen::class) {
        subclass(Screen.Auth::class)
        subclass(Screen.BookmarksFeed::class)
        subclass(Screen.AddBookmark::class)
        subclass(Screen.Url::class)
        subclass(Screen.Settings::class)
        subclass(Screen.Tags::class)
      }
    }
  }
}
