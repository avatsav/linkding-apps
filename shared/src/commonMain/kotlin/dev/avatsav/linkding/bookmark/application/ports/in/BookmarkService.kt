package dev.avatsav.linkding.bookmark.application.ports.`in`

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError


interface BookmarkService {
    suspend fun get(
        startIndex: Int = 0,
        limit: Int = 50,
        query: String = ""
    ): Either<BookmarkError, BookmarkList>

    suspend fun save(bookmark: Bookmark): Either<BookmarkSaveError, Bookmark>

    suspend fun update(bookmarkId: Long, updatedBookmark: Bookmark): Either<BookmarkSaveError, Bookmark>

    suspend fun get(bookmarkId: Long): Either<BookmarkError, Bookmark>

    suspend fun archive(bookmarkId: Long): Either<BookmarkError, Unit>

    suspend fun unarchive(bookmarkId: Long): Either<BookmarkError, Unit>

    suspend fun delete(bookmarkId: Long): Either<BookmarkError, Unit>
}