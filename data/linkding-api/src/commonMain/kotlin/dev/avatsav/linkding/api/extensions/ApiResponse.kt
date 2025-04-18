package dev.avatsav.linkding.api.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.ApiResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse

internal fun <T> ApiResponse<T, LinkdingErrorResponse>.toLinkdingResult():
  Result<T, LinkdingError> =
  when (this) {
    is ApiResponse.Success -> Ok(this.body)
    is ApiResponse.Error ->
      when (this) {
        is ApiResponse.ClientError ->
          Err(LinkdingError.Unauthorized(derivedErrorMessage("Unauthorized")))
        is ApiResponse.ConnectivityError ->
          Err(LinkdingError.Connectivity(derivedErrorMessage("Connectivity")))
        else -> Err(LinkdingError.Other(derivedErrorMessage("Unknown")))
      }
  }

internal fun ApiResponse.Error<LinkdingErrorResponse>.derivedErrorMessage(
  default: String = ""
): String = body?.detail ?: message ?: default
