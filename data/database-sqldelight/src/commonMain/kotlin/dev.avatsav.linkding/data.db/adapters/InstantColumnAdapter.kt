package dev.avatsav.linkding.data.db.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

internal object InstantColumnAdapter : ColumnAdapter<Instant, String> {
  override fun encode(value: Instant): String = value.toString()

  override fun decode(databaseValue: String): Instant = Instant.parse(databaseValue)
}
