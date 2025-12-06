package dev.avatsav.linkding.navigation

import dev.avatsav.linkding.navigation.impl.NavigatorImpl
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavigatorImplTest {

  private val screenA = TestScreens.screenA()
  private val screenB = TestScreens.screenB()
  private val screenC = TestScreens.screenC()

  private val backStack = ScreenBackStack(screenA)
  private val resultHandler = NavigationResultHandler()
  private val navigator = NavigatorImpl(backStack, resultHandler)

  @Test
  fun `currentScreen returns top of backstack`() {
    navigator.currentScreen shouldBe screenA
  }

  @Test
  fun `currentScreen returns null for empty backstack`() {
    val emptyBackStack = ScreenBackStack()
    val nav = NavigatorImpl(emptyBackStack, resultHandler)
    nav.currentScreen.shouldBeNull()
  }

  @Test
  fun `goTo adds screen to backstack`() {
    navigator.goTo(screenB)

    navigator.currentScreen shouldBe screenB
    navigator.peekBackStack() shouldHaveSize 2
    navigator.peekBackStack() shouldContainExactly listOf(screenA, screenB)
  }

  @Test
  fun `goTo returns true on success`() {
    navigator.goTo(screenB).shouldBeTrue()
  }

  @Test
  fun `goTo with Url screen calls onOpenUrl handler`() {
    var openedUrl: String? = null
    val nav =
      NavigatorImpl(
        backStack,
        resultHandler,
        onOpenUrl = { url ->
          openedUrl = url
          true
        },
      )

    nav.goTo(Screen.Url("https://example.com"))

    openedUrl shouldBe "https://example.com"
  }

  @Test
  fun `goTo with Url screen does not add to backstack when handler present`() {
    val nav = NavigatorImpl(backStack, resultHandler, onOpenUrl = { true })
    val initialSize = nav.peekBackStack().size

    nav.goTo(Screen.Url("https://example.com"))

    nav.peekBackStack() shouldHaveSize initialSize
  }

  @Test
  fun `goTo with Url screen adds to backstack when no handler`() {
    // No onOpenUrl handler
    val urlScreen = Screen.Url("https://example.com")
    navigator.goTo(urlScreen)

    navigator.currentScreen shouldBe urlScreen
  }

  @Test
  fun `pop removes top screen`() {
    navigator.goTo(screenB)
    navigator.goTo(screenC)

    navigator.pop()

    navigator.currentScreen shouldBe screenB
    navigator.peekBackStack() shouldHaveSize 2
  }

  @Test
  fun `pop returns new current screen`() {
    navigator.goTo(screenB)

    val result = navigator.pop()

    result shouldBe screenA
  }

  @Test
  fun `pop does not pop root screen`() {
    // Only one screen in backstack
    val result = navigator.pop()

    result.shouldBeNull()
    navigator.currentScreen shouldBe screenA
    navigator.peekBackStack() shouldHaveSize 1
  }

  @Test
  fun `pop with result routes to result handler`() {
    val callerScreen = TestScreens.screenA("caller")
    val callerBackStack = ScreenBackStack(callerScreen)
    val handler = NavigationResultHandler()
    val nav = NavigatorImpl(callerBackStack, handler)

    // Push target screen
    nav.goTo(TestScreens.resultScreen())

    // Prepare the caller to receive result
    handler.prepareForResult(callerScreen.key, NavigationResultHandler.RESULT_KEY_FROM_POP)

    // Pop with result
    nav.pop(TestScreens.Results.dismissed)

    // Verify result was routed
    handler.awaitResult(callerScreen.key, NavigationResultHandler.RESULT_KEY_FROM_POP) shouldBe
      TestScreens.Results.dismissed
  }

  @Test
  fun `pop with result does nothing when no caller awaiting`() {
    navigator.goTo(TestScreens.resultScreen())

    // Pop with result but no one prepared to receive
    navigator.pop(TestScreens.Results.dismissed)

    // Verify no result stored (since no one was awaiting)
    resultHandler
      .awaitResult(screenA.key, NavigationResultHandler.RESULT_KEY_FROM_POP)
      .shouldBeNull()
  }

  @Test
  fun `peek returns current screen without modifying backstack`() {
    navigator.goTo(screenB)

    navigator.peek() shouldBe screenB
    navigator.peekBackStack() shouldHaveSize 2
  }

  @Test
  fun `peekBackStack returns copy of backstack`() {
    navigator.goTo(screenB)
    navigator.goTo(screenC)

    val peeked = navigator.peekBackStack()

    peeked shouldContainExactly listOf(screenA, screenB, screenC)
  }

  @Test
  fun `resetRoot clears backstack and sets new root`() {
    navigator.goTo(screenB)
    navigator.goTo(screenC)

    val newRoot = TestScreens.screenWithParam(42)
    navigator.resetRoot(newRoot)

    navigator.peekBackStack() shouldHaveSize 1
    navigator.currentScreen shouldBe newRoot
  }

  @Test
  fun `resetRoot returns true`() {
    navigator.resetRoot(screenB).shouldBeTrue()
  }

  @Test
  fun `multiple navigations work correctly`() {
    val paramScreen = TestScreens.screenWithParam(1)
    navigator.goTo(screenB)
    navigator.goTo(screenC)
    navigator.pop()
    navigator.goTo(paramScreen)

    navigator.peekBackStack() shouldContainExactly listOf(screenA, screenB, paramScreen)
  }
}
