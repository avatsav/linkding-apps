package dev.avatsav.linkding

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.bookmark.adapter.out.LinkdingBookmarkRepository
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.application.ports.out.BookmarkRepository
import dev.avatsav.linkding.bookmark.application.services.LinkdingBookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.data.Credentials
import dev.avatsav.linkding.data.CredentialsStore
import dev.avatsav.linkding.inject.httpClient
import io.ktor.client.HttpClient
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class BookmarkServiceTest {

    private val httpClient: HttpClient = httpClient(true)

    private val bookmarkRepository: BookmarkRepository = LinkdingBookmarkRepository(httpClient)

    @OptIn(ExperimentalSettingsApi::class)
    private val credentialsStore: CredentialsStore =
        CredentialsStore(MapSettings().toFlowSettings())

    private val bookmarkService: BookmarkService =
        LinkdingBookmarkService(bookmarkRepository, credentialsStore)

    @BeforeTest
    fun init() {
        runBlocking {
            credentialsStore.set(
                Credentials(
                    apiKey = "TODO", url = "TODO"
                )
            )
        }
    }

    @Test
    fun testGetBookmarks() {
        runBlocking {
            bookmarkService.get().map { println(it) }.mapLeft {
                when (it) {
                    is BookmarkError.CouldNotGetBookmark -> println(it.message)
                    BookmarkError.CredentialsNotSetup -> println("Credentials are not setup")
                }
            }
        }
    }
}