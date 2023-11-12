package dev.avatsav.linkding.api.models

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class
LinkdingTag(
    val id: Long,
    val name: String,
    @SerialName("date_added") val dateAdded: Instant,
)
