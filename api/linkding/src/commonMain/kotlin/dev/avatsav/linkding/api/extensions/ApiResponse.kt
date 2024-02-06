package dev.avatsav.linkding.api.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.ApiResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingErrorResponse

internal inline fun <T> ApiResponse<T, LinkdingErrorResponse>.toLinkdingResult(): Result<T, LinkdingError> {
    return when (this) {
        is ApiResponse.Success -> Ok(this.body)
        is ApiResponse.Error -> when (this) {
            is ApiResponse.ClientError -> Err(LinkdingError.Unauthorized(derivedErrorMessage("Unauthorized")))
            is ApiResponse.ConnectivityError -> Err(LinkdingError.Connectivity(derivedErrorMessage("Connectivity")))
            else -> Err(LinkdingError.Other(derivedErrorMessage("Unknown")))
        }
    }
}

internal inline fun ApiResponse.Error<LinkdingErrorResponse>.derivedErrorMessage(default: String = ""): String {
    return body?.detail ?: message ?: default
}
