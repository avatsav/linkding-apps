package dev.avatsav.linkding.navigation

/**
 * A type-safe navigator constrained to a specific screen type.
 *
 * This interface is returned by [rememberResultNavigator] and ensures compile-time safety that you
 * can only navigate to the expected screen type. This prevents accidentally navigating to a
 * different screen and expecting results of the wrong type.
 *
 * ## Usage
 *
 * ```kotlin
 * val tagsNavigator = rememberResultNavigator<Screen.Tags, Screen.Tags.Result> { result ->
 *   // Handle result
 * }
 *
 * // Both of these are equivalent:
 * tagsNavigator.goTo(Screen.Tags())
 * tagsNavigator(Screen.Tags())
 *
 * // This would NOT compile - type safety!
 * // tagsNavigator.goTo(Screen.Settings()) // Error: Type mismatch
 * ```
 *
 * @param S The screen type this navigator can navigate to
 * @see rememberResultNavigator
 */
interface ScreenNavigator<in S : Screen> {

  /**
   * Navigate to the given screen.
   *
   * @param screen The screen to navigate to
   * @return `true` if navigation was successful, `false` otherwise
   */
  fun goTo(screen: S): Boolean

  /**
   * Navigate to the given screen using invoke operator syntax.
   *
   * This allows for more concise navigation: `tagsNavigator(Screen.Tags())` instead of
   * `tagsNavigator.goTo(Screen.Tags())`.
   *
   * @param screen The screen to navigate to
   * @return `true` if navigation was successful, `false` otherwise
   */
  operator fun invoke(screen: S): Boolean = goTo(screen)
}
