package dev.avatsav.linkding

import com.russhwolf.settings.MapSettings
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.services.LinkdingBookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.data.Credentials
import dev.avatsav.linkding.data.CredentialsStore
import dev.avatsav.linkding.inject.httpClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class BookmarkServiceTest {

    private val httpClient: HttpClient = httpClient(true)

    private val credentialsStore: CredentialsStore = CredentialsStore(MapSettings())

    private val bookmarkService: BookmarkService =
        LinkdingBookmarkService(httpClient, credentialsStore)

    @BeforeTest
    fun init() {
        credentialsStore.set(
            Credentials(
                // TODO: Setup using test containers ?
                apiKey = "fakeCredentials",
                url = "http://bookmarks.local"
            )
        )
    }

    @Test
    fun testGetBookmarks() {
        runBlocking {
            bookmarkService.getBookmarks(startIndex = 0)
                .map { println(it) }
                .mapLeft {
                    when (it) {
                        is BookmarkError.CouldNotGetBookmark -> println(it.message)
                        BookmarkError.CredentialsNotSetup -> println("Credentials are not setup")
                    }
                }
        }
    }
}