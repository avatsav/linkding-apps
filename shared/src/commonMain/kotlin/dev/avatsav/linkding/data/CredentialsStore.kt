package dev.avatsav.linkding.data

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

internal const val CredentialsKey = "credentials"

object CredentialsNotSetup

@Serializable
data class Credentials(val url: String, val apiKey: String)

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class CredentialsStore {

    private val settings: Settings = Settings()

    fun get(): Effect<CredentialsNotSetup, Credentials> = effect {
        settings.decodeValueOrNull(Credentials.serializer(), CredentialsKey)
            ?: shift(CredentialsNotSetup)
    }

    fun set(credentials: Credentials) {
        settings.encodeValue(Credentials.serializer(), CredentialsKey, credentials)
    }
}