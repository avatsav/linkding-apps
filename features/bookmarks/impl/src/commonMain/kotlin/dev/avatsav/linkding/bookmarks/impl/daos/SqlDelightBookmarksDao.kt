package dev.avatsav.linkding.bookmarks.impl.daos

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightBookmarksDao(private val db: Database) : BookmarksDao {

  override fun insert(bookmark: Bookmark) {
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

  override fun insert(bookmarks: List<Bookmark>) {
    db.transaction {
      for (entity in bookmarks) {
        insert(entity)
      }
    }
  }

  override fun update(bookmark: Bookmark) {
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

  override fun upsert(bookmark: Bookmark) {
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

  override fun upsert(bookmarks: List<Bookmark>) {
    db.bookmarksQueries.transaction {
      for (entity in bookmarks) {
        upsert(entity)
      }
    }
  }

  override fun delete(id: Long) {
    val _ = db.bookmarksQueries.delete(id)
  }

  override fun deleteAll() {
    val _ = db.bookmarksQueries.deleteAll()
  }
}
