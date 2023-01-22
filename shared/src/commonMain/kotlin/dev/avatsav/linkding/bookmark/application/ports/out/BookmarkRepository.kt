package dev.avatsav.linkding.bookmark.application.ports.out

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkFilter
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.SaveBookmark

interface BookmarkRepository {
    suspend fun fetch(
        baseUrl: String,
        token: String,
        offset: Int,
        limit: Int,
        filter: BookmarkFilter,
        query: String
    ): Either<BookmarkError, BookmarkList>

    suspend fun fetch(
        baseUrl: String,
        token: String,
        id: Long
    ): Either<BookmarkError, Bookmark>

    suspend fun save(
        baseUrl: String,
        token: String,
        saveBookmark: SaveBookmark
    ): Either<BookmarkSaveError, Bookmark>
}