package dev.avatsav.linkding.bookmark.application.services

import arrow.core.Either
import arrow.core.left
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.data.CredentialsStore

class LinkdingBookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val credentialsStore: CredentialsStore
) : BookmarkService {

    override suspend fun get(
        startIndex: Int,
        limit: Int,
        query: String
    ): Either<BookmarkError, BookmarkList> {
        return credentialsStore.get().toEither()
            .map { credentials ->
                return bookmarkRepository.fetch(
                    credentials.url,
                    credentials.apiKey,
                    startIndex,
                    limit,
                    query
                )
            }
            .mapLeft { return@get BookmarkError.CredentialsNotSetup.left() }
    }


    override suspend fun get(bookmarkId: Long): Either<BookmarkError, Bookmark> {
        return credentialsStore.get().toEither()
            .map { credentials ->
                return bookmarkRepository.fetch(
                    credentials.url,
                    credentials.apiKey,
                    bookmarkId
                )
            }
            .mapLeft { return@get BookmarkError.CredentialsNotSetup.left() }
    }

    override suspend fun save(bookmark: Bookmark): Either<BookmarkSaveError, Bookmark> {
        return credentialsStore.get().toEither()
            .map { credentials ->
                return bookmarkRepository.save(
                    credentials.url,
                    credentials.apiKey,
                    bookmark
                )
            }
            .mapLeft { return@save BookmarkSaveError.CredentialsNotSetup.left() }
    }

    override suspend fun update(
        bookmarkId: Long,
        updatedBookmark: Bookmark
    ): Either<BookmarkSaveError, Bookmark> {
        TODO("Not yet implemented")
    }

    override suspend fun archive(bookmarkId: Long): Either<BookmarkError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unarchive(bookmarkId: Long): Either<BookmarkError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(bookmarkId: Long): Either<BookmarkError, Unit> {
        TODO("Not yet implemented")
    }
}