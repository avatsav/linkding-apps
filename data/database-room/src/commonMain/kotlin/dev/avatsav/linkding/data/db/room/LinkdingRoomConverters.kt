package dev.avatsav.linkding.data.db.room

import androidx.room3.TypeConverter
import kotlin.time.Instant

object LinkdingRoomConverters {
  @TypeConverter fun fromInstant(value: Instant?): String? = value?.toString()

  @TypeConverter fun toInstant(value: String?): Instant? = value?.let(Instant::parse)

  @TypeConverter fun fromStringSet(value: Set<String>): String = value.joinToString(separator = ",")

  @TypeConverter fun toStringSet(value: String): Set<String> =
    if (value.isBlank()) {
      emptySet()
    } else {
      value.split(',').toSet()
    }
}
