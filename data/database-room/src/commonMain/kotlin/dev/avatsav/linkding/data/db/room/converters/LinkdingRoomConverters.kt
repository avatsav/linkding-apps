package dev.avatsav.linkding.data.db.room.converters

import androidx.room3.ColumnTypeConverter
import kotlin.time.Instant

object LinkdingRoomConverters {
  @ColumnTypeConverter fun fromInstant(value: Instant?): String? = value?.toString()

  @ColumnTypeConverter
  fun toInstant(value: String?): Instant? = value?.let(Instant.Companion::parse)

  @ColumnTypeConverter
  fun fromStringSet(value: Set<String>): String = value.joinToString(separator = ",")

  @ColumnTypeConverter
  fun toStringSet(value: String): Set<String> =
    if (value.isBlank()) {
      emptySet()
    } else {
      value.split(',').toSet()
    }
}
