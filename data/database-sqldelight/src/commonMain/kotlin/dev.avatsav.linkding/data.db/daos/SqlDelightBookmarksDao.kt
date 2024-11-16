package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.model.Bookmark
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightBookmarksDao(private val db: Database) : BookmarksDao {

    override fun insert(bookmark: Bookmark) {
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
        db.bookmarksQueries.delete(id)
    }

    override fun deleteAll() {
        db.bookmarksQueries.deleteAll()
    }
}
