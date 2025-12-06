package dev.avatsav.linkding.navigation.fake

import app.cash.turbine.Turbine
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.Navigator
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenBackStack

/**
 * A fake [Navigator] implementation for testing that records all navigation events.
 *
 * Uses Turbine internally to provide async-safe assertions for navigation calls.
 *
 * ## Usage
 *
 * ```kotlin
 * @Test
 * fun testNavigation() = runTest {
 *   val navigator = FakeNavigator(TestScreen.Home)
 *
 *   // Perform navigation
 *   navigator.goTo(TestScreen.Details)
 *
 *   // Assert
 *   navigator.awaitGoTo() shouldBe TestScreen.Details
 *   navigator.assertGoToIsEmpty()
 * }
 * ```
 */
class FakeNavigator(vararg initialScreens: Screen) : Navigator {

  private val backStack = ScreenBackStack(*initialScreens)

  private val goToEvents = Turbine<GoToEvent>()
  private val popEvents = Turbine<PopEvent>()
  private val resetRootEvents = Turbine<ResetRootEvent>()

  override val currentScreen: Screen?
    get() = backStack.lastOrNull()

  override fun goTo(screen: Screen): Boolean {
    val currentTop = backStack.lastOrNull()
    // Don't navigate to the same screen
    if (currentTop == screen) {
      goToEvents.add(GoToEvent(screen, success = false))
      return false
    }
    backStack.add(screen)
    goToEvents.add(GoToEvent(screen, success = true))
    return true
  }

  override fun pop(result: NavResult?): Screen? {
    if (backStack.size <= 1) {
      popEvents.add(PopEvent(poppedScreen = null, result = result))
      return null
    }
    val poppedScreen = backStack.removeLastOrNull()
    popEvents.add(PopEvent(poppedScreen = poppedScreen, result = result))
    return backStack.lastOrNull()
  }

  override fun peek(): Screen? = backStack.lastOrNull()

  override fun peekBackStack(): List<Screen> = backStack.toList()

  override fun resetRoot(newRoot: Screen): Boolean {
    val oldScreens = backStack.toList()
    backStack.clear()
    backStack.add(newRoot)
    resetRootEvents.add(ResetRootEvent(newRoot = newRoot, oldScreens = oldScreens))
    return true
  }

  // ===== Assertion methods =====

  /** Await and return the next goTo event. */
  suspend fun awaitGoTo(): GoToEvent = goToEvents.awaitItem()

  /** Await and return the next screen navigated to. */
  suspend fun awaitNextScreen(): Screen = goToEvents.awaitItem().screen

  /** Await and return the next pop event. */
  suspend fun awaitPop(): PopEvent = popEvents.awaitItem()

  /** Await and return the next resetRoot event. */
  suspend fun awaitResetRoot(): ResetRootEvent = resetRootEvents.awaitItem()

  /** Assert that no goTo events are pending. */
  fun assertGoToIsEmpty() {
    goToEvents.ensureAllEventsConsumed()
  }

  /** Assert that no pop events are pending. */
  fun assertPopIsEmpty() {
    popEvents.ensureAllEventsConsumed()
  }

  /** Assert that no resetRoot events are pending. */
  fun assertResetRootIsEmpty() {
    resetRootEvents.ensureAllEventsConsumed()
  }

  /** Cancel all pending events. Call this in test cleanup. */
  suspend fun cancel() {
    goToEvents.cancel()
    popEvents.cancel()
    resetRootEvents.cancel()
  }

  /** Event recorded when [goTo] is called. */
  data class GoToEvent(val screen: Screen, val success: Boolean)

  /** Event recorded when [pop] is called. */
  data class PopEvent(val poppedScreen: Screen?, val result: NavResult? = null)

  /** Event recorded when [resetRoot] is called. */
  data class ResetRootEvent(val newRoot: Screen, val oldScreens: List<Screen>)
}
