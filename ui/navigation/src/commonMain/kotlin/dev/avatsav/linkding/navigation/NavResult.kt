package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Immutable

/**
 * Marker interface for navigation results passed between screens.
 *
 * Implement this interface for any result type that can be returned from a screen when it is popped
 * from the backstack. Results are passed via [Navigator.pop] and received via
 * [rememberResultNavigator].
 *
 * Example:
 * ```kotlin
 * sealed interface TagsResult : NavResult {
 *   data class Confirmed(val tags: List<Tag>) : TagsResult
 *   data object Dismissed : TagsResult
 * }
 * ```
 *
 * @see ScreenWithResult
 * @see Navigator.pop
 * @see rememberResultNavigator
 */
@Immutable interface NavResult

/**
 * Marker interface for screens that return a typed result.
 *
 * Implement this interface on [Screen] subclasses that can return results when popped. The type
 * parameter [R] specifies the result type, enabling compile-time type safety when using
 * [rememberResultNavigator].
 *
 * Example:
 * ```kotlin
 * @Serializable
 * data class Tags(
 *   val selectedTagIds: List<Long> = emptyList(),
 *   override val key: String = Uuid.random().toString(),
 * ) : Screen, ScreenWithResult<Tags.Result> {
 *
 *   sealed interface Result : NavResult {
 *     data class Confirmed(val selectedTags: List<Tag>) : Result
 *     data object Dismissed : Result
 *   }
 * }
 * ```
 *
 * @param R The result type this screen can return (must extend [NavResult])
 * @see NavResult
 * @see rememberResultNavigator
 */
interface ScreenWithResult<R : NavResult>
