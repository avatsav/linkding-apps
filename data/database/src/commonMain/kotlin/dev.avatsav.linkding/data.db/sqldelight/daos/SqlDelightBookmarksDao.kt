package dev.avatsav.linkding.data.db.sqldelight.daos

import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import me.tatarka.inject.annotations.Inject

@Inject
class SqlDelightBookmarksDao(private val db: Database) : BookmarksDao {

    override fun insert(entity: Bookmark) {
        db.bookmarksQueries.insert(
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

    override fun insert(entities: List<Bookmark>) {
        db.transaction {
            for (entity in entities) {
                insert(entity)
            }
        }
    }

    override fun update(entity: Bookmark) {
        db.bookmarksQueries.update(
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

    override fun delete(id: Long) {
        db.bookmarksQueries.delete(id)
    }

    override fun deleteAll() {
        db.bookmarksQueries.deleteAll()
    }
}
