package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import dev.avatsav.linkding.navigation.impl.NavigatorImpl

/**
 * Core navigation interface for managing routes and the backstack.
 *
 * ```kotlin
 * val navigator = LocalNavigator.current
 * navigator.goTo(Route.Settings)
 * navigator.pop(result)
 * ```
 */
interface Navigator {
  /** Current route on top of the backstack, or `null` if empty. */
  val currentRoute: Route?

  /** Navigate to a route. [Route.Url] is handled by platform URL handler. */
  @IgnorableReturnValue fun goTo(route: Route): Boolean

  /** Pop current route, optionally returning a [result]. Root cannot be popped. */
  @IgnorableReturnValue fun pop(result: NavResult? = null): Route?

  /** Peek at current route without modifying backstack. */
  fun peek(): Route?

  /** Get a copy of the entire backstack. */
  fun peekBackStack(): List<Route>

  /** Clear backstack and set a new root route. */
  @IgnorableReturnValue fun resetRoot(newRoot: Route): Boolean
}

/** No-op [Navigator] for previews and tests. */
class NoOpNavigator : Navigator {
  override val currentRoute: Route? = null

  override fun goTo(route: Route): Boolean = false

  override fun pop(result: NavResult?): Route? = null

  override fun peek(): Route? = null

  override fun peekBackStack(): List<Route> = emptyList()

  override fun resetRoot(newRoot: Route): Boolean = false
}

/**
 * Create and remember a [Navigator] instance.
 *
 * @param backStack The route back stack to manage.
 * @param onOpenUrl Handler for [Route.Url] navigation.
 * @param onRootPop Called when [Navigator.pop] is invoked on the root route. Use this to finish the
 *   activity or exit the app.
 *
 * ```kotlin
 * val backStack = rememberRouteBackStack(config, Route.BookmarksFeed)
 * val navigator = rememberNavigator(
 *   backStack = backStack,
 *   onOpenUrl = { url -> openUrl(url) },
 *   onRootPop = { activity.finish() }
 * )
 * ```
 */
@Composable
fun rememberNavigator(
  backStack: RouteBackStack,
  resultHandler: NavigationResultHandler,
  onOpenUrl: ((String) -> Boolean)? = null,
  onRootPop: () -> Unit = {},
): Navigator {
  return remember(resultHandler) { NavigatorImpl(backStack, resultHandler, onOpenUrl, onRootPop) }
}

/**
 * Provides [LocalNavigator] and result handling for the content tree.
 *
 * ```kotlin
 * NavigatorCompositionLocals(navigator) {
 *   NavDisplay(backStack = backStack, onBack = { navigator.pop() })
 * }
 * ```
 */
@Composable
fun NavigatorCompositionLocals(
  navigator: Navigator,
  resultHandler: NavigationResultHandler,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalNavigator provides navigator,
    LocalNavigationResultHandler provides resultHandler,
    content = content,
  )
}
