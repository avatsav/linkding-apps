package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.PendingBookmark
import kotlinx.coroutines.flow.Flow

interface PendingBookmarksDao {
  fun observeAll(accountId: String): Flow<List<PendingBookmark>>

  fun observeById(accountId: String, id: Long): Flow<PendingBookmark?>

  suspend fun getById(accountId: String, id: Long): PendingBookmark?

  suspend fun getAll(accountId: String): List<PendingBookmark>

  suspend fun insert(accountId: String, bookmark: PendingBookmark): Long

  suspend fun update(accountId: String, bookmark: PendingBookmark)

  suspend fun delete(accountId: String, id: Long)
}
