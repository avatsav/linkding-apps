package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dev.avatsav.linkding.navigation.impl.NavigatorImpl

/**
 * Core navigation interface for managing screen navigation and backstack operations.
 *
 * The Navigator provides methods to navigate between screens, manage the backstack, and pass
 * results back when popping screens. It integrates with the navigation result system to enable
 * type-safe result passing between screens.
 *
 * ## Setup
 *
 * Create a navigator with [rememberNavigator] and provide it via [NavigatorCompositionLocals]:
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
 * val navigator = rememberNavigator(backStack, onOpenUrl)
 *
 * NavigatorCompositionLocals(navigator) {
 *   // Your app content - LocalNavigator is now available
 *   NavDisplay(backStack = backStack, ...)
 * }
 * ```
 *
 * ## Usage
 *
 * Access the navigator in any composable:
 * ```kotlin
 * val navigator = LocalNavigator.current
 * navigator.goTo(Screen.Settings)
 * ```
 *
 * For type-safe result navigation, use [rememberResultNavigator] instead of direct navigation.
 *
 * @see LocalNavigator
 * @see NavigatorCompositionLocals
 * @see rememberResultNavigator
 * @see ScreenBackStack
 */
interface Navigator {
  /**
   * The current screen on top of the backstack.
   *
   * Returns `null` if the backstack is empty.
   */
  val currentScreen: Screen?

  /**
   * Navigate to a new screen, pushing it onto the backstack.
   *
   * Special handling is applied for [Screen.Url] which delegates to the platform's URL handler
   * (e.g., Chrome Custom Tabs on Android).
   *
   * @param screen The screen to navigate to
   * @return `true` if navigation was successful, `false` otherwise
   */
  fun goTo(screen: Screen): Boolean

  /**
   * Pop the current screen from the backstack, optionally passing a result.
   *
   * When a [result] is provided, it will be routed to any [ScreenNavigator] that initiated the
   * navigation to the current screen via [rememberResultNavigator].
   *
   * The root screen cannot be popped - attempting to do so returns `null`.
   *
   * @param result Optional result to pass back to the previous screen
   * @return The new current screen after popping, or `null` if pop failed
   */
  fun pop(result: NavResult? = null): Screen?

  /**
   * Peek at the current screen without modifying the backstack.
   *
   * @return The current screen, or `null` if backstack is empty
   */
  fun peek(): Screen?

  /**
   * Get a copy of the entire backstack.
   *
   * @return List of all screens in the backstack, from bottom to top
   */
  fun peekBackStack(): List<Screen>

  /**
   * Reset the backstack to a new root screen.
   *
   * Clears the entire backstack and sets the given screen as the new root. Useful for scenarios
   * like logout where you want to reset to the auth screen.
   *
   * @param newRoot The screen to set as the new root
   * @return `true` if the reset was successful
   */
  fun resetRoot(newRoot: Screen): Boolean
}

/**
 * A no-op implementation of [Navigator] for use in previews and tests.
 *
 * All navigation operations return failure indicators and the backstack is always empty.
 */
class NoOpNavigator : Navigator {
  override val currentScreen: Screen? = null

  override fun goTo(screen: Screen): Boolean = false

  override fun pop(result: NavResult?): Screen? = null

  override fun peek(): Screen? = null

  override fun peekBackStack(): List<Screen> = emptyList()

  override fun resetRoot(newRoot: Screen): Boolean = false
}

/**
 * Create and remember a [Navigator] instance.
 *
 * The navigator automatically manages result passing between screens. Use with
 * [NavigatorCompositionLocals] to provide the required composition locals.
 *
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
 * val navigator = rememberNavigator(backStack, onOpenUrl)
 *
 * NavigatorCompositionLocals(navigator) {
 *   NavDisplay(backStack = backStack, ...)
 * }
 * ```
 *
 * @param backStack The [ScreenBackStack] to manage
 * @param onOpenUrl Optional callback to handle [Screen.Url] navigation (e.g., open in browser)
 * @return A remembered [Navigator] instance
 * @see NavigatorCompositionLocals
 * @see rememberScreenBackStack
 */
@Composable
fun rememberNavigator(
  backStack: ScreenBackStack,
  onOpenUrl: ((String) -> Boolean)? = null,
): Navigator {
  val resultHandler = rememberNavigationResultHandler()
  return remember(resultHandler) { NavigatorImpl(backStack, resultHandler, onOpenUrl) }
}

/**
 * Provides navigation-related composition locals for the content.
 *
 * This composable sets up [LocalNavigator] and the internal result handler required for
 * [rememberResultNavigator] to work. Wrap your app's navigation content with this.
 *
 * ```kotlin
 * val backStack = rememberScreenBackStack(savedStateConfig, startScreen)
 * val navigator = rememberNavigator(backStack, onOpenUrl)
 *
 * NavigatorCompositionLocals(navigator) {
 *   NavDisplay(
 *     backStack = backStack,
 *     onBack = { navigator.pop() },
 *     ...
 *   )
 * }
 * ```
 *
 * @param navigator The navigator instance created via [rememberNavigator]
 * @param content The composable content that will have access to navigation locals
 * @see rememberNavigator
 * @see rememberScreenBackStack
 * @see LocalNavigator
 */
@Composable
fun NavigatorCompositionLocals(navigator: Navigator, content: @Composable () -> Unit) {
  val resultHandler = (navigator as NavigatorImpl).resultHandler
  CompositionLocalProvider(
    LocalNavigator provides navigator,
    LocalNavigationResultHandler provides resultHandler,
    content = content,
  )
}
