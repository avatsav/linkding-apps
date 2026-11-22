package dev.avatsav.linkding.bookmarks.ui.add

import androidx.paging.PagingConfig
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.BookmarksRepository
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.bookmarks.api.interactors.GetBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UpdateBookmark
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.SaveResult
import dev.avatsav.linkding.data.model.UpdateBookmark as UpdateBookmarkModel
import dev.avatsav.linkding.navigation.Route
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class AddBookmarkPresenterTest {
  private val dispatcher = StandardTestDispatcher()
  private val repository = RecordingBookmarks()
  private val pending = RecordingPending()
  private lateinit var presenter: AddBookmarkPresenter

  @Before fun setUp() = Dispatchers.setMain(dispatcher)

  @After
  fun tearDown() {
    presenter.close()
    dispatcher.scheduler.runCurrent()
    Dispatchers.resetMain()
  }

  @Test
  fun `editing URL cancels the old lookup without waiting for the next debounce`() =
    runTest(dispatcher) {
      start(Route.AddBookmark.Shared(oldUrl))
      advanceTimeBy(500)
      runCurrent()
      assertEquals(listOf(oldUrl), repository.checkedUrls)

      presenter.eventSink(AddBookmarkUiEvent.UrlChanged(newUrl))
      runCurrent()
      repository.oldMatch.complete(
        Bookmark(
          id = 42,
          url = oldUrl,
          urlHost = "example.com",
          title = "Old match",
          description = "",
        )
      )
      runCurrent()
      assertEquals(listOf(oldUrl), repository.checkedUrls, "New URL is still debouncing")
      assertNull(presenter.models.value.existingBookmark)
      assertNull(presenter.effects.tryReceive().getOrNull())

      advanceTimeBy(500)
      runCurrent()
      assertEquals(listOf(oldUrl, newUrl), repository.checkedUrls)
      assertEquals(newUrl, presenter.models.value.checkedUrl)
      presenter.eventSink(AddBookmarkUiEvent.Save(newUrl, "New bookmark", null, null, emptyList()))
      runCurrent()
      assertEquals(listOf(newUrl), repository.createdUrls)
    }

  @Test
  fun `a draft becoming a conflict blocks Save in the presenter`() =
    runTest(dispatcher) {
      pending.row.value =
        PendingBookmark(
          id = 1,
          bookmark = SaveBookmark(oldUrl),
          createdAt = Instant.fromEpochSeconds(0),
        )
      start(Route.AddBookmark.Pending(1))
      runCurrent()
      assertEquals(1L, presenter.models.value.pendingBookmark?.id)

      pending.row.value = pending.row.value?.copy(state = PendingBookmark.State.AlreadyExists)
      runCurrent()
      assertTrue(presenter.models.value.pendingEditBlocked)
      presenter.eventSink(AddBookmarkUiEvent.Save(oldUrl, "Typed title", null, null, emptyList()))
      runCurrent()
      assertTrue(pending.edits.isEmpty())
    }

  private fun start(route: Route.AddBookmark) {
    val dispatchers = AppCoroutineDispatchers(dispatcher, dispatcher, dispatcher)
    presenter =
      AddBookmarkPresenter(
        route,
        AddBookmark(repository, dispatchers),
        GetBookmark(repository, dispatchers),
        UpdateBookmark(repository, dispatchers),
        CheckBookmarkUrl(repository, dispatchers),
        pending,
      )
    assertNull(presenter.models.value.existingBookmark)
  }

  private class RecordingBookmarks : BookmarksRepository {
    val oldMatch = CompletableDeferred<Bookmark>()
    val checkedUrls = mutableListOf<String>()
    val createdUrls = mutableListOf<String>()

    override suspend fun checkUrl(url: String): Result<CheckUrlResult, BookmarkError> {
      checkedUrls += url
      val existing = if (url == oldUrl) oldMatch.await() else null
      return Ok(CheckUrlResult(existing, url, null, null))
    }

    override suspend fun saveBookmark(saveBookmark: SaveBookmark) =
      Ok(SaveResult(1)).also { createdUrls += saveBookmark.url }

    override suspend fun updateBookmark(updateBookmark: UpdateBookmarkModel) =
      error("Unexpected update")

    override suspend fun getBookmark(id: Long) = error("Unexpected lookup")

    override suspend fun archiveBookmark(id: Long) = error("Unexpected archive")

    override suspend fun unarchiveBookmark(id: Long) = error("Unexpected unarchive")

    override suspend fun deleteBookmark(id: Long) = error("Unexpected delete")

    override fun getBookmarksPaged(
      cached: Boolean,
      pagingConfig: PagingConfig,
      query: String,
      category: BookmarkCategory,
      tags: List<String>,
    ) = error("Unexpected paging")
  }

  private class RecordingPending : PendingBookmarks {
    val row = MutableStateFlow<PendingBookmark?>(null)
    val edits = mutableListOf<SaveBookmark>()

    override fun observe(id: Long) = row

    override suspend fun edit(id: Long, bookmark: SaveBookmark): Boolean = edits.add(bookmark)

    override fun observe() = error("Only one draft should be observed")

    override suspend fun get(id: Long) = error("Use the live draft observer")

    override suspend fun create(bookmark: SaveBookmark) = error("Unexpected creation")

    override suspend fun discard(id: Long) = error("Unexpected discard")

    override suspend fun retry(id: Long?) = error("Unexpected retry")

    override suspend fun overwrite(id: Long) = error("Unexpected overwrite")

    override suspend fun runUploader() = error("Unexpected upload")
  }

  companion object {
    private const val oldUrl = "https://example.com/old"
    private const val newUrl = "https://example.com/new"
  }
}
