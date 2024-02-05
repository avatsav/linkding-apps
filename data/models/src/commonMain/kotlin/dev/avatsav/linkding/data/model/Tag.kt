package dev.avatsav.linkding.data.model

import kotlinx.datetime.Instant

data class Tag(
    val id: Long,
    val name: String,
    val dateAdded: Instant,
)
