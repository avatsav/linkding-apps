package dev.avatsav.linkding.data.bookmarks

import arrow.core.Either
import arrow.core.left
import dev.avatsav.linkding.data.configuration.ConfigurationStore
import dev.avatsav.linkding.domain.Bookmark
import dev.avatsav.linkding.domain.BookmarkError
import dev.avatsav.linkding.domain.BookmarkFilter
import dev.avatsav.linkding.domain.BookmarkList
import dev.avatsav.linkding.domain.BookmarkSaveError
import dev.avatsav.linkding.domain.Configuration
import dev.avatsav.linkding.domain.SaveBookmark
import dev.avatsav.linkding.domain.TestConnectionError

interface BookmarksRepository {
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

class LinkdingBookmarksRepository(
    private val bookmarksDataSource: BookmarksDataSource,
    private val configurationStore: ConfigurationStore
) : BookmarksRepository {

    override suspend fun get(
        offset: Int,
        limit: Int,
        filter: BookmarkFilter,
        query: String
    ): Either<BookmarkError, BookmarkList> {
        return configurationStore.get().toEither().map { credentials ->
            return bookmarksDataSource.fetch(
                credentials.url, credentials.apiKey, offset, limit, filter, query
            )
        }.mapLeft { return@get BookmarkError.ConfigurationNotSetup.left() }
    }


    override suspend fun get(bookmarkId: Long): Either<BookmarkError, Bookmark> {
        return configurationStore.get().toEither().map { credentials ->
            return bookmarksDataSource.fetch(
                credentials.url, credentials.apiKey, bookmarkId
            )
        }.mapLeft { return@get BookmarkError.ConfigurationNotSetup.left() }
    }

    override suspend fun testConnection(configuration: Configuration): Either<TestConnectionError, Configuration> {
        return bookmarksDataSource.fetch(
            configuration.url,
            configuration.apiKey,
            0,
            1,
            BookmarkFilter.None, ""
        ).fold(ifLeft = { Either.Left(TestConnectionError) },
            ifRight = { Either.Right(configuration) })
    }

    override suspend fun save(saveBookmark: SaveBookmark): Either<BookmarkSaveError, Bookmark> {
        return configurationStore.get().toEither().map { configuration ->
            return bookmarksDataSource.save(
                configuration.url, configuration.apiKey, saveBookmark
            )
        }.mapLeft { return@save BookmarkSaveError.ConfigurationNotSetup.left() }
    }

    override suspend fun update(
        bookmarkId: Long, updatedBookmark: Bookmark
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