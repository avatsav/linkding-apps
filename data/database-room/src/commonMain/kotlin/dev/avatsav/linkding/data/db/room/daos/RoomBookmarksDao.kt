package dev.avatsav.linkding.data.db.room.daos

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import dev.avatsav.linkding.data.db.room.entities.BookmarkEntity

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
@Suppress("TooManyFunctions")
abstract class RoomBookmarksDao {
  @Query("SELECT * FROM bookmarks ORDER BY id ASC")
  abstract fun bookmarksPagingSource(): PagingSource<Int, BookmarkEntity>

  @Query("SELECT * FROM bookmarks ORDER BY id ASC")
  abstract suspend fun selectAll(): List<BookmarkEntity>

  @Query("SELECT count(*) FROM bookmarks") abstract suspend fun countBookmarks(): Long

  @Insert(onConflict = OnConflictStrategy.ABORT)
  protected abstract suspend fun insertEntry(bookmark: BookmarkEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  protected abstract suspend fun insertIgnore(bookmark: BookmarkEntity): Long

  @Query(
    """
      UPDATE bookmarks SET
        url = :url,
        urlHost = :urlHost,
        title = :title,
        description = :description,
        archived = :archived,
        unread = :unread,
        tags = :tags,
        added = :added,
        modified = :modified
      WHERE linkding_id = :linkdingId
    """
  )
  @Suppress("LongParameterList")
  protected abstract suspend fun updateExisting(
    linkdingId: Long,
    url: String,
    urlHost: String,
    title: String,
    description: String,
    archived: Boolean,
    unread: Boolean,
    tags: Set<String>,
    added: kotlin.time.Instant?,
    modified: kotlin.time.Instant?,
  ): Int

  @Query("DELETE FROM bookmarks WHERE linkding_id = :linkdingId")
  abstract suspend fun deleteByLinkdingId(linkdingId: Long)

  @Query("DELETE FROM bookmarks") abstract suspend fun deleteAll()

  @Transaction
  open suspend fun insertAll(bookmarks: List<BookmarkEntity>) {
    for (bookmark in bookmarks) {
      insertEntry(bookmark)
    }
  }

  @IgnorableReturnValue
  open suspend fun update(bookmark: BookmarkEntity): Int {
    return updateExisting(
      linkdingId = bookmark.linkdingId,
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

  @Transaction
  open suspend fun upsert(bookmark: BookmarkEntity): Long {
    val updated = update(bookmark)
    return if (updated == 0) insertIgnore(bookmark) else updated.toLong()
  }

  @IgnorableReturnValue
  @Transaction
  open suspend fun upsertAll(bookmarks: List<BookmarkEntity>): List<Long> {
    return bookmarks.map { upsert(it) }
  }

  @Transaction
  open suspend fun replaceAll(bookmarks: List<BookmarkEntity>) {
    deleteAll()
    insertAll(bookmarks)
  }
}
