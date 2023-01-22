package dev.avatsav.linkding

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.data.bookmarks.BookmarksDataSource
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.bookmarks.LinkdingBookmarksDataSource
import dev.avatsav.linkding.data.bookmarks.LinkdingBookmarksRepository
import dev.avatsav.linkding.data.configuration.ConfigurationStore
import dev.avatsav.linkding.domain.BookmarkError
import dev.avatsav.linkding.domain.BookmarkSaveError
import dev.avatsav.linkding.domain.Configuration
import dev.avatsav.linkding.domain.SaveBookmark
import dev.avatsav.linkding.inject.httpClient
import io.ktor.client.HttpClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class BookmarkServiceTest {

    private val httpClient: HttpClient = httpClient(true)

    private val bookmarksDataSource: BookmarksDataSource = LinkdingBookmarksDataSource(httpClient)

    private val configurationStore: ConfigurationStore =
        ConfigurationStore(MapSettings().toFlowSettings())

    private val bookmarksRepository: BookmarksRepository =
        LinkdingBookmarksRepository(bookmarksDataSource, configurationStore)

    @BeforeTest
    fun init() {
        runBlocking {
            configurationStore.set(
                Configuration(
                    apiKey = "TODO", url = "TODO"
                )
            )
        }
    }

    @Test
    fun testGetBookmarks() {
        runBlocking {
            bookmarksRepository.get().map { println("Success: $it") }.mapLeft {
                when (it) {
                    is BookmarkError.CouldNotGetBookmark -> println("Error: ${it.message}")
                    BookmarkError.ConfigurationNotSetup -> println("Credentials are not setup")
                }
            }
        }
    }

    @Test
    fun testSaveBookmarks() {
        runBlocking {
            val saveBookmark = SaveBookmark(url = "https://melix.github.io/blog/tags/gradle.html")
            bookmarksRepository.save(saveBookmark).map { println("Success: $it") }.mapLeft {
                when (it) {
                    BookmarkSaveError.ConfigurationNotSetup -> println("Credentials are not setup")
                    is BookmarkSaveError.CouldNotSaveBookmark -> println("Error: ${it.message}")
                }
            }
        }
    }
}