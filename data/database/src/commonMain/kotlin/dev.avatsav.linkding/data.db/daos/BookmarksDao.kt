package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.Bookmark

interface BookmarksDao {
    fun insert(entity: Bookmark)
    fun insert(entities: List<Bookmark>)
    fun update(entity: Bookmark)
    fun delete(id: Long)
    fun deleteAll()
}
