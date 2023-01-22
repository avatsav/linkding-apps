package dev.avatsav.linkding.bookmark.application.ports.`in`

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkFilter
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.SaveBookmark
import dev.avatsav.linkding.bookmark.domain.TestConnectionError
import dev.avatsav.linkding.data.Configuration


interface BookmarkService {
    suspend fun get(
        offset: Int = 0,
        limit: Int = 10,
        filter: BookmarkFilter = BookmarkFilter.None,
        query: String = ""
    ): Either<BookmarkError, BookmarkList>

    suspend fun testConnection(configuration: Configuration): Either<TestConnectionError, Configuration>

    suspend fun save(saveBookmark: SaveBookmark): Either<BookmarkSaveError, Bookmark>

    suspend fun update(
        bookmarkId: Long, updatedBookmark: Bookmark
    ): Either<BookmarkSaveError, Bookmark>

    suspend fun get(bookmarkId: Long): Either<BookmarkError, Bookmark>

    suspend fun archive(bookmarkId: Long): Either<BookmarkError, Unit>

    suspend fun unarchive(bookmarkId: Long): Either<BookmarkError, Unit>

    suspend fun delete(bookmarkId: Long): Either<BookmarkError, Unit>
}