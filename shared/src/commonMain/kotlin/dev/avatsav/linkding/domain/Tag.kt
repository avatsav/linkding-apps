package dev.avatsav.linkding.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Long,
    val name: String,
    @SerialName("date_added") val dateAdded: Instant
)
