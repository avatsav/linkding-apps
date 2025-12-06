package dev.avatsav.linkding.navigation

/**
 * Type-safe navigator constrained to a specific route type.
 *
 * Returned by [rememberResultNavigator]. Supports both `goTo()` and invoke operator syntax.
 */
interface RouteNavigator<in R : Route> {
  fun goTo(route: R): Boolean

  operator fun invoke(route: R): Boolean = goTo(route)
}
