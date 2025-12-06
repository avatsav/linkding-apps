package dev.avatsav.linkding.navigation

/**
 * A type-safe navigator constrained to a specific screen type.
 *
 * This interface is returned by [rememberResultNavigator] and ensures compile-time safety that you
 * can only navigate to the expected screen type.
 *
 * @param S The screen type this navigator can navigate to
 */
interface ScreenNavigator<in S : Screen> {
  /**
   * Navigate to the given screen.
   *
   * @param screen The screen to navigate to
   * @return true if navigation was successful
   */
  fun goTo(screen: S): Boolean
}
