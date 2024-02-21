package dev.avatsav.linkding.data.db.sqldelight.daos

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import me.tatarka.inject.annotations.Inject

@Inject
class SqlDelightBookmarksDao(private val db: Database) : BookmarksDao {

    override fun insert(bookmark: Bookmark) {
        db.bookmarksQueries.insert(
            id = bookmark.id,
            external_id = bookmark.externalId,
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
        db.bookmarksQueries.update(
            id = bookmark.id,
            external_id = bookmark.externalId,
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

    override fun upsert(entity: Bookmark) {
        db.bookmarksQueries.upsert(
            id = entity.id,
            external_id = entity.externalId,
            url = entity.url,
            urlHost = entity.urlHost,
            title = entity.title,
            description = entity.description,
            archived = entity.archived,
            unread = entity.unread,
            tags = entity.tags,
            added = entity.added,
            modified = entity.modified,
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
        db.bookmarksQueries.delete(id)
    }

    override fun deleteAll() {
        db.bookmarksQueries.deleteAll()
    }
}
