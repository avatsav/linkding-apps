package dev.avatsav.linkding.data.db.daos

import androidx.paging.PagingSource
import dev.avatsav.linkding.data.db.room.LinkdingRoomDatabase
import dev.avatsav.linkding.data.db.room.MappingPagingSource
import dev.avatsav.linkding.data.db.room.toModel
import dev.avatsav.linkding.data.db.room.toRoomEntity
import dev.avatsav.linkding.data.model.Bookmark
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RoomPagingBookmarksDao(private val db: LinkdingRoomDatabase) : PagingBookmarksDao {
  override fun bookmarksPagingSource(): PagingSource<Int, Bookmark> =
    MappingPagingSource(db.bookmarksQueries().bookmarksPagingSource(), mapper = { it.toModel() })

  override suspend fun refresh(bookmarks: List<Bookmark>) {
    db.bookmarksQueries().replaceAll(bookmarks.map { it.toRoomEntity() })
  }

  override suspend fun append(bookmarks: List<Bookmark>) {
    db.bookmarksQueries().upsertAll(bookmarks.map { it.toRoomEntity() })
  }

  override suspend fun countBookmarks(): Long = db.bookmarksQueries().countBookmarks()
}
