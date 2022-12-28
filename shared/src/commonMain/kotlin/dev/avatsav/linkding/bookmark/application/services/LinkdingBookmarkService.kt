package dev.avatsav.linkding.bookmark.application.services

import arrow.core.Either
import arrow.core.left
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkList
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.TestConnectionError
import dev.avatsav.linkding.data.Configuration
import dev.avatsav.linkding.data.ConfigurationStore

class LinkdingBookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val configurationStore: ConfigurationStore
) : BookmarkService {

    override suspend fun get(
        startIndex: Int,
        limit: Int,
        query: String
    ): Either<BookmarkError, BookmarkList> {
        return configurationStore.get().toEither()
            .map { credentials ->
                return bookmarkRepository.fetch(
                    credentials.url,
                    credentials.apiKey,
                    startIndex,
                    limit,
                    query
                )
            }
            .mapLeft { return@get BookmarkError.ConfigurationNotSetup.left() }
    }


    override suspend fun get(bookmarkId: Long): Either<BookmarkError, Bookmark> {
        return configurationStore.get().toEither()
            .map { credentials ->
                return bookmarkRepository.fetch(
                    credentials.url,
                    credentials.apiKey,
                    bookmarkId
                )
            }
            .mapLeft { return@get BookmarkError.ConfigurationNotSetup.left() }
    }

    override suspend fun testConnection(configuration: Configuration): Either<TestConnectionError, Configuration> {
        return bookmarkRepository.fetch(
            configuration.url,
            configuration.apiKey,
            0, 1, ""
        ).fold(ifLeft = { Either.Left(TestConnectionError) },
            ifRight = { Either.Right(configuration) })
    }

    override suspend fun save(bookmark: Bookmark): Either<BookmarkSaveError, Bookmark> {
        return configurationStore.get().toEither()
            .map { configuration ->
                return bookmarkRepository.save(
                    configuration.url,
                    configuration.apiKey,
                    bookmark
                )
            }
            .mapLeft { return@save BookmarkSaveError.ConfigurationNotSetup.left() }
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