package dev.avatsav.linkding.navigation

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavigationResultHandlerTest {

  private val handler = NavigationResultHandler()

  @Test
  fun `prepareForResult registers expectation`() {
    handler.prepareForResult("caller-1", "result-key")

    // Sending a result should work after preparation
    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    handler.awaitResult("caller-1", "result-key") shouldBe TestRoutes.Results.dismissed
  }

  @Test
  fun `sendResult only sends to awaiting callers`() {
    // Don't prepare - just send
    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    // Result should not be stored since no one was awaiting
    handler.awaitResult("caller-1", "result-key").shouldBeNull()
  }

  @Test
  fun `awaitResult consumes the result`() {
    handler.prepareForResult("caller-1", "result-key")
    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    // First await should return the result
    handler.awaitResult("caller-1", "result-key") shouldBe TestRoutes.Results.dismissed

    // Second await should return null (consumed)
    handler.awaitResult("caller-1", "result-key").shouldBeNull()
  }

  @Test
  fun `awaitResult returns null when no result pending`() {
    handler.prepareForResult("caller-1", "result-key")

    // No result sent yet
    handler.awaitResult("caller-1", "result-key").shouldBeNull()
  }

  @Test
  fun `cancelAwait cleans up pending results`() {
    handler.prepareForResult("caller-1", "result-key")
    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    // Cancel before consuming
    handler.cancelAwait("caller-1", "result-key")

    // Result should be gone
    handler.awaitResult("caller-1", "result-key").shouldBeNull()
  }

  @Test
  fun `cancelAwait cleans up awaiting state`() {
    handler.prepareForResult("caller-1", "result-key")
    handler.cancelAwait("caller-1", "result-key")

    // Sending after cancel should not store the result
    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    handler.awaitResult("caller-1", "result-key").shouldBeNull()
  }

  @Test
  fun `multiple callers can await results independently`() {
    handler.prepareForResult("caller-1", "result-key")
    handler.prepareForResult("caller-2", "result-key")

    val result1 = TestRoutes.Results.success(emptyList())
    val result2 = TestRoutes.Results.dismissed

    handler.sendResult("caller-1", "result-key", result1)
    handler.sendResult("caller-2", "result-key", result2)

    handler.awaitResult("caller-1", "result-key") shouldBe result1
    handler.awaitResult("caller-2", "result-key") shouldBe result2
  }

  @Test
  fun `same caller can have multiple result keys`() {
    handler.prepareForResult("caller-1", "key-a")
    handler.prepareForResult("caller-1", "key-b")

    val resultA = TestRoutes.Results.success(emptyList())
    val resultB = TestRoutes.Results.dismissed

    handler.sendResult("caller-1", "key-a", resultA)
    handler.sendResult("caller-1", "key-b", resultB)

    handler.awaitResult("caller-1", "key-a") shouldBe resultA
    handler.awaitResult("caller-1", "key-b") shouldBe resultB
  }

  @Test
  fun `result isolation - only intended caller receives result`() {
    handler.prepareForResult("caller-1", "result-key")
    // caller-2 did NOT prepare

    handler.sendResult("caller-1", "result-key", TestRoutes.Results.dismissed)

    // caller-1 should receive the result
    handler.awaitResult("caller-1", "result-key") shouldBe TestRoutes.Results.dismissed

    // caller-2 should not receive anything
    handler.awaitResult("caller-2", "result-key").shouldBeNull()
  }
}
