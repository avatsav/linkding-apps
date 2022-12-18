package dev.avatsav.linkding.bookmark.application.ports.`in`

import arrow.core.Either
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList

interface BookmarkService {
    suspend fun getBookmarks(startIndex: Int = 0, limit: Int = 50, query: String = ""): Either<BookmarkError, BookmarkList>
}