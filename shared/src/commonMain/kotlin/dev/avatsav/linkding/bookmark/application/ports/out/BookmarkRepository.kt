package dev.avatsav.linkding.bookmark.application.ports.out

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError

interface BookmarkRepository {
    suspend fun fetch(
        baseUrl: String,
        token: String,
        startIndex: Int,
        limit: Int,
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
        bookmark: Bookmark
    ): Either<BookmarkSaveError, Bookmark>
}