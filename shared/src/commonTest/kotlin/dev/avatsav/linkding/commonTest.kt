package dev.avatsav.linkding

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.bookmark.adapter.out.LinkdingBookmarkRepository
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.application.services.LinkdingBookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.SaveBookmark
import dev.avatsav.linkding.data.Configuration
import dev.avatsav.linkding.data.ConfigurationStore
import dev.avatsav.linkding.inject.httpClient
import io.ktor.client.HttpClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class BookmarkServiceTest {

    private val httpClient: HttpClient = httpClient(true)

    private val bookmarkRepository: BookmarkRepository = LinkdingBookmarkRepository(httpClient)

    private val configurationStore: ConfigurationStore =
        ConfigurationStore(MapSettings().toFlowSettings())

    private val bookmarkService: BookmarkService =
        LinkdingBookmarkService(bookmarkRepository, configurationStore)

    @BeforeTest
    fun init() {
        runBlocking {
            configurationStore.set(
                Configuration(
                    apiKey = "TODO",
                    url = "TODO"
                )
            )
        }
    }

    @Test
    fun testGetBookmarks() {
        runBlocking {
            bookmarkService.get().map { println("Success: $it") }.mapLeft {
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
            bookmarkService.save(saveBookmark).map { println("Success: $it") }.mapLeft {
                when (it) {
                    BookmarkSaveError.ConfigurationNotSetup -> println("Credentials are not setup")
                    is BookmarkSaveError.CouldNotSaveBookmark -> println("Error: ${it.message}")
                }
            }
        }
    }
}