package dev.avatsav.linkding.api.extensions

import com.github.michaelbull.result.fold
import dev.avatsav.linkding.api.models.ApiResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest

class HttpFailureTest {
  @Test
  fun `only 401 and 403 are authentication errors`() = runTest {
    for (code in listOf(400, 401, 403, 422, 500)) {
      val client =
        HttpClient(MockEngine { respond("", HttpStatusCode.fromValue(code)) }) {
          expectSuccess = true
        }
      try {
        val result =
          client
            .get<String, LinkdingErrorResponse> { url("https://example.com") }
            .toLinkdingResult()
        result.fold(
          success = { error("Expected HTTP failure") },
          failure = { failure ->
            if (code == 401 || code == 403) {
              assertIs<LinkdingError.Unauthorized>(failure)
            } else {
              assertIs<LinkdingError.Other>(failure)
            }
            Unit
          },
        )
      } finally {
        client.close()
      }
    }
  }

  @Test
  fun `cancellation is never converted to an offline failure`() = runTest {
    val client = HttpClient(MockEngine { throw CancellationException("Stopped") })
    try {
      try {
        val unexpected = client.get<String, LinkdingErrorResponse> { url("https://example.com") }
        error("Expected cancellation, got $unexpected")
      } catch (_: CancellationException) {
        // Expected: the lifecycle, not the network fallback, owns cancellation.
      }
    } finally {
      client.close()
    }
  }

  @Test
  fun `request timeout is a transport failure`() = runTest {
    val client = HttpClient(MockEngine { request -> throw HttpRequestTimeoutException(request) })
    try {
      assertIs<ApiResponse.ConnectivityError>(
        client.get<String, LinkdingErrorResponse> { url("https://example.com") }
      )
    } finally {
      client.close()
    }
  }

  @Test
  fun `unparseable response cannot prove a POST was rejected`() {
    ApiResponse.UnknownError(IllegalStateException("Response lost"))
      .toLinkdingResult()
      .fold(
        success = { error("Expected failure") },
        failure = { failure ->
          assertTrue((failure as LinkdingError.Other).requestMayHaveSucceeded)
        },
      )
  }
}
