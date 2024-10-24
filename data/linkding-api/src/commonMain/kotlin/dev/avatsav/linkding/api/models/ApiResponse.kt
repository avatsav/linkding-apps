package dev.avatsav.linkding.api.models

import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

internal sealed interface ApiResponse<out T, out E> {
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>
    sealed class Error<E>(open val message: String?, open val body: E?) : ApiResponse<Nothing, E>

    data class ClientError<E>(val code: Int, override val body: E?) : Error<E>(null, body)
    data class ServerError<E>(val code: Int, override val body: E?) : Error<E>(null, body)
    data class SerializationError(val exception: SerializationException) : Error<Nothing>(exception.message, null)

    data class ConnectivityError(val exception: IOException) : Error<Nothing>(exception.message, null)

    data class UnknownError(val throwable: Throwable) : Error<Nothing>(throwable.message, null)
}
