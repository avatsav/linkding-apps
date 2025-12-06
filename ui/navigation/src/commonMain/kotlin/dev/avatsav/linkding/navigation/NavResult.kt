package dev.avatsav.linkding.navigation

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
interface NavResult
