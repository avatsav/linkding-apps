package dev.avatsav.linkding.auth.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.AuthError

interface AuthRepository {

    suspend fun authenticate(
        hostUrl: String,
        apiKey: String,
    ): Result<Unit, AuthError>
}
