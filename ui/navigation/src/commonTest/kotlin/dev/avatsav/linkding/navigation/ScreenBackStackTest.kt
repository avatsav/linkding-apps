package dev.avatsav.linkding.navigation

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ScreenBackStackTest {

  private val screenA = TestScreens.screenA()
  private val screenB = TestScreens.screenB()
  private val screenC = TestScreens.screenC()

  @Test
  fun `empty constructor creates empty backstack`() {
    val backStack = ScreenBackStack()
    backStack.shouldBeEmpty()
  }

  @Test
  fun `vararg constructor initializes with screens`() {
    val backStack = ScreenBackStack(screenA, screenB, screenC)

    backStack shouldHaveSize 3
    backStack shouldContainExactly listOf(screenA, screenB, screenC)
  }

  @Test
  fun `add appends screen to backstack`() {
    val backStack = ScreenBackStack(screenA)

    backStack.add(screenB)

    backStack shouldHaveSize 2
    backStack.last() shouldBe screenB
  }

  @Test
  fun `plusAssign appends screen to backstack`() {
    val backStack = ScreenBackStack(screenA)

    backStack += screenB

    backStack shouldHaveSize 2
    backStack.last() shouldBe screenB
  }

  @Test
  fun `removeLast removes top screen`() {
    val backStack = ScreenBackStack(screenA, screenB)

    val removed = backStack.removeLast()

    removed shouldBe screenB
    backStack shouldHaveSize 1
    backStack.last() shouldBe screenA
  }

  @Test
  fun `clear removes all screens`() {
    val backStack = ScreenBackStack(screenA, screenB, screenC)

    backStack.clear()

    backStack.shouldBeEmpty()
  }

  @Test
  fun `lastOrNull returns last screen`() {
    val backStack = ScreenBackStack(screenA, screenB)

    backStack.lastOrNull() shouldBe screenB
  }

  @Test
  fun `lastOrNull returns null for empty backstack`() {
    val backStack = ScreenBackStack()

    backStack.lastOrNull() shouldBe null
  }

  @Test
  fun `size returns number of screens`() {
    val backStack = ScreenBackStack(screenA, screenB)

    backStack.size shouldBe 2
  }

  @Test
  fun `isEmpty returns true for empty backstack`() {
    val backStack = ScreenBackStack()

    backStack.isEmpty().shouldBeTrue()
  }

  @Test
  fun `isEmpty returns false for non-empty backstack`() {
    val backStack = ScreenBackStack(screenA)

    backStack.isEmpty().shouldBeFalse()
  }

  @Test
  fun `toList returns copy of screens`() {
    val backStack = ScreenBackStack(screenA, screenB)

    val list = backStack.toList()

    list shouldContainExactly listOf(screenA, screenB)
  }

  @Test
  fun `index access works correctly`() {
    val backStack = ScreenBackStack(screenA, screenB, screenC)

    backStack[0] shouldBe screenA
    backStack[1] shouldBe screenB
    backStack[2] shouldBe screenC
  }

  @Test
  fun `contains returns true for existing screen`() {
    val backStack = ScreenBackStack(screenA)

    backStack.contains(screenA).shouldBeTrue()
  }

  @Test
  fun `contains returns false for non-existing screen`() {
    val backStack = ScreenBackStack(screenA)

    backStack.contains(screenB).shouldBeFalse()
  }
}
