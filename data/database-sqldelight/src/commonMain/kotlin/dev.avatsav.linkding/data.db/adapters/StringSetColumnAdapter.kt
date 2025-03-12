package dev.avatsav.linkding.data.db.adapters

import app.cash.sqldelight.ColumnAdapter

internal object StringSetColumnAdapter : ColumnAdapter<Set<String>, String> {
  override fun decode(databaseValue: String): Set<String> =
    if (databaseValue.isEmpty()) {
      setOf()
    } else {
      databaseValue.split(",").toSet()
    }

  override fun encode(value: Set<String>): String = value.joinToString(separator = ",")
}
