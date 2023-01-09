package dev.avatsav.linkding.extensions

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

internal suspend inline fun <reified T, reified E> HttpClient.requestApiResponse(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T, E> = try {
    val response = request { block() }
    ApiResponse.Success(response.body())
} catch (e: SerializationException) { // client-side serialization errors
    ApiResponse.SerializationError(e)
} catch (e: ClientRequestException) { // 4xx errors
    ApiResponse.ClientError(e.response.status.value, e.errorBody())
} catch (e: ServerResponseException) { // 5xx errors
    ApiResponse.ServerError(e.response.status.value, e.errorBody())
} catch (e: IOException) { // network connectivity errors
    ApiResponse.ConnectivityError(e)
} catch (t: Throwable) { // Everything else
    ApiResponse.UnknownError(t)
}

internal sealed interface ApiResponse<out T, out E> {
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>
    sealed class Error<E>(open val body: E?, open val message: String?) : ApiResponse<Nothing, E>
    data class ClientError<E>(val code: Int, override val body: E?) : Error<E>(body, null)
    data class ServerError<E>(val code: Int, override val body: E?) : Error<E>(body, null)
    data class SerializationError(val exception: SerializationException) :
        Error<Nothing>(null, exception.message)

    data class ConnectivityError(val exception: IOException) :
        Error<Nothing>(null, exception.message)

    data class UnknownError(val throwable: Throwable) : Error<Nothing>(null, throwable.message)
}

internal suspend inline fun <reified E> ResponseException.errorBody(): E? = try {
    response.body()
} catch (e: Exception) {
    null
}