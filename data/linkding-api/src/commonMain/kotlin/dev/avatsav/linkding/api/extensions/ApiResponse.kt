package dev.avatsav.linkding.api.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.ApiResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse
import io.ktor.http.HttpStatusCode

internal fun <T> ApiResponse<T, LinkdingErrorResponse>.toLinkdingResult():
  Result<T, LinkdingError> =
  when (this) {
    is Success -> Ok(this.body)
    is ApiResponse.Error ->
      when (this) {
        is ApiResponse.ClientError ->
          if (code == HttpStatusCode.Unauthorized.value || code == HttpStatusCode.Forbidden.value) {
            Err(LinkdingError.Unauthorized(derivedErrorMessage("Unauthorized")))
          } else {
            Err(LinkdingError.Other(derivedErrorMessage("Request rejected ($code)")))
          }
        is ApiResponse.ConnectivityError ->
          Err(LinkdingError.Connectivity(derivedErrorMessage("Connectivity")))
        else ->
          Err(
            LinkdingError.Other(
              derivedErrorMessage("Unknown"),
              requestMayHaveSucceeded = true,
            )
          )
      }
  }

internal fun ApiResponse.Error<LinkdingErrorResponse>.derivedErrorMessage(
  default: String = ""
): String = body?.detail ?: message ?: default
