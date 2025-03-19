package dev.avatsav.linkding.api.extensions

import dev.avatsav.linkding.api.models.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

internal suspend inline fun <reified T, reified E> HttpClient.get(
  block: HttpRequestBuilder.() -> Unit
) = request<T, E>(HttpMethod.Get, block)

internal suspend inline fun <reified T, reified E> HttpClient.post(
  block: HttpRequestBuilder.() -> Unit
) = request<T, E>(HttpMethod.Post, block)

internal suspend inline fun <reified T, reified E> HttpClient.delete(
  block: HttpRequestBuilder.() -> Unit
) = request<T, E>(HttpMethod.Delete, block)

@Suppress("TooGenericExceptionCaught")
internal suspend inline fun <reified T, reified E> HttpClient.request(
  httpMethod: HttpMethod,
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T, E> =
  try {
    val response =
      this@request.request {
        method = httpMethod
        block()
      }
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

@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal suspend inline fun <reified E> ResponseException.errorBody(): E? =
  try {
    response.body()
  } catch (e: Exception) {
    null
  }
