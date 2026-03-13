package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.model.Bookmark
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightBookmarksDao(private val db: Database) : BookmarksDao {

  override suspend fun insert(bookmark: Bookmark) {
    insertBlocking(bookmark)
  }

  override suspend fun insert(bookmarks: List<Bookmark>) {
    db.transaction {
      for (entity in bookmarks) {
        insertBlocking(entity)
      }
    }
  }

  override suspend fun update(bookmark: Bookmark) {
    val _ =
      db.bookmarksQueries.update(
        linkding_id = bookmark.id,
        url = bookmark.url,
        urlHost = bookmark.urlHost,
        title = bookmark.title,
        description = bookmark.description,
        archived = bookmark.archived,
        unread = bookmark.unread,
        tags = bookmark.tags,
        added = bookmark.added,
        modified = bookmark.modified,
      )
  }

  override suspend fun upsert(bookmark: Bookmark) {
    upsertBlocking(bookmark)
  }

  override suspend fun upsert(bookmarks: List<Bookmark>) {
    db.bookmarksQueries.transaction {
      for (entity in bookmarks) {
        upsertBlocking(entity)
      }
    }
  }

  override suspend fun delete(id: Long) {
    val _ = db.bookmarksQueries.delete(id)
  }

  override suspend fun deleteAll() {
    val _ = db.bookmarksQueries.deleteAll()
  }

  private fun insertBlocking(bookmark: Bookmark) {
    val _ =
      db.bookmarksQueries.insert(
        linkding_id = bookmark.id,
        url = bookmark.url,
        urlHost = bookmark.urlHost,
        title = bookmark.title,
        description = bookmark.description,
        archived = bookmark.archived,
        unread = bookmark.unread,
        tags = bookmark.tags,
        added = bookmark.added,
        modified = bookmark.modified,
      )
  }

  private fun upsertBlocking(bookmark: Bookmark) {
    val _ =
      db.bookmarksQueries.upsert(
        linkding_id = bookmark.id,
        url = bookmark.url,
        urlHost = bookmark.urlHost,
        title = bookmark.title,
        description = bookmark.description,
        archived = bookmark.archived,
        unread = bookmark.unread,
        tags = bookmark.tags,
        added = bookmark.added,
        modified = bookmark.modified,
      )
  }
}
