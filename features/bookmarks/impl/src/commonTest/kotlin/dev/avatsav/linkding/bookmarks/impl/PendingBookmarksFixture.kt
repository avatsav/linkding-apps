package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.models.LinkdingBookmark
import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory
import dev.avatsav.linkding.api.models.LinkdingBookmarksResponse
import dev.avatsav.linkding.api.models.LinkdingCheckUrlResponse
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingSaveBookmarkRequest
import dev.avatsav.linkding.api.models.LinkdingUpdateBookmarkRequest
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.internet.LocalNetworkAccess
import dev.avatsav.linkding.prefs.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class PendingBookmarksFixture {
  val store = MemoryPendingDao()
  val cache = MemoryBookmarksDao()
  val api = FakeApi()
  val preferences = FakePreferences()
  val online = MutableStateFlow(true)
  val access = FakeLocalNetworkAccess()
  private val coordinator =
    PendingBookmarksCoordinator(
      object : ConnectivityObserver {
        override val observeIsOnline = online
      }
    )

  fun newQueue(config: ApiConfig = initialConfig) =
    DefaultPendingBookmarks(
      store,
      preferences,
      config,
      coordinator,
      PendingBookmarksUploader(
        store,
        cache,
        api,
        BookmarkMapper(),
        preferences,
        config,
        access,
        coordinator,
      ),
    )

  val queue = newQueue()

  class FakeLocalNetworkAccess : LocalNetworkAccess() {
    override val permissionRequired = true
    public override val permissionGranted = MutableStateFlow(true)
    var onLookup: suspend () -> Unit = {}

    override suspend fun isLocalServer(hostUrl: String): Boolean {
      onLookup()
      return true
    }

    override fun refresh() = Unit
  }

  class MemoryPendingDao : PendingBookmarksDao {
    val rows = MutableStateFlow<List<Pair<String, PendingBookmark>>>(emptyList())
    private var nextId = 1L
    var onRead: suspend () -> Unit = {}
    var onInsert: suspend () -> Unit = {}
    var onUpdate: suspend (PendingBookmark) -> Unit = {}

    override fun observeAll(accountId: String): Flow<List<PendingBookmark>> = rows.map { items ->
      items.filter { it.first == accountId }.map { it.second }
    }

    override suspend fun getAll(accountId: String): List<PendingBookmark> {
      val snapshot = rows.value.filter { it.first == accountId }.map { it.second }
      onRead()
      return snapshot
    }

    override suspend fun insert(accountId: String, bookmark: PendingBookmark): Long {
      onInsert()
      val id = nextId++
      rows.value += accountId to bookmark.copy(id = id)
      return id
    }

    override suspend fun update(accountId: String, bookmark: PendingBookmark) {
      rows.value =
        rows.value.map {
          if (it.first == accountId && it.second.id == bookmark.id) accountId to bookmark else it
        }
      onUpdate(bookmark)
    }

    override suspend fun delete(accountId: String, id: Long) {
      rows.value = rows.value.filterNot { it.first == accountId && it.second.id == id }
    }
  }

  class MemoryBookmarksDao : BookmarksDao {
    val rows = mutableListOf<Bookmark>()

    override suspend fun insert(bookmark: Bookmark) {
      rows.add(bookmark)
    }

    override suspend fun insert(bookmarks: List<Bookmark>) {
      rows.addAll(bookmarks)
    }

    override suspend fun update(bookmark: Bookmark) {
      rows.removeAll { it.id == bookmark.id }
      rows.add(bookmark)
    }

    override suspend fun upsert(bookmark: Bookmark): Long {
      update(bookmark)
      return bookmark.id
    }

    override suspend fun upsert(bookmarks: List<Bookmark>) = bookmarks.map { upsert(it) }

    override suspend fun delete(id: Long) {
      rows.removeAll { it.id == id }
    }

    override suspend fun deleteAll() {
      rows.clear()
    }
  }

  class FakePreferences : AppPreferences {
    var config: ApiConfig? = initialConfig
    var localAccountId = "account"

    override fun getApiConfig() = config

    override fun getAccountId() = localAccountId

    override fun observeApiConfig() = MutableStateFlow(config)

    override suspend fun setApiConfig(apiConfig: ApiConfig?) {
      if (config != apiConfig) localAccountId = "replacement-account"
      config = apiConfig
    }

    override fun observeAppTheme() = MutableStateFlow(AppTheme.System)

    override suspend fun setAppTheme(appTheme: AppTheme) = Unit

    override fun observeUseDynamicColors() = MutableStateFlow(false)

    override suspend fun toggleUseDynamicColors() = Unit
  }

  class FakeApi : LinkdingBookmarksApi {
    var online = false
    var checkError: LinkdingError? = null
    var saveError: LinkdingError? = null
    var existing: LinkdingBookmark? = null
    var onCheck: suspend () -> Unit = {}
    var onSave: suspend () -> Unit = {}
    val checkedUrls = mutableListOf<String>()
    val sent = mutableListOf<LinkdingSaveBookmarkRequest>()

    override suspend fun checkUrl(url: String): Result<LinkdingCheckUrlResponse, LinkdingError> {
      checkedUrls += url
      onCheck()
      val error = checkError
      return when {
        error != null -> Err(error)
        !online -> Err(LinkdingError.Connectivity())
        else ->
          Ok(
            LinkdingCheckUrlResponse(
              existing,
              LinkdingCheckUrlResponse.Metadata(url, null, null),
            )
          )
      }
    }

    override suspend fun saveBookmark(
      request: LinkdingSaveBookmarkRequest
    ): Result<LinkdingBookmark, LinkdingError> {
      sent += request
      onSave()
      saveError?.let {
        return Err(it)
      }
      return Ok(
        remote.copy(
          title = request.title.orEmpty(),
          notes = request.notes.orEmpty(),
          tagNames = request.tagNames,
        )
      )
    }

    override suspend fun getBookmarks(
      offset: Int,
      limit: Int,
      query: String,
      category: LinkdingBookmarkCategory,
      tags: List<String>,
    ): Result<LinkdingBookmarksResponse, LinkdingError> = error("Unexpected read")

    override suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingError> =
      error("Unexpected read")

    override suspend fun updateBookmark(
      id: Long,
      request: LinkdingUpdateBookmarkRequest,
    ): Result<LinkdingBookmark, LinkdingError> = error("Must not edit server bookmarks")

    override suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingError> =
      error("Must not archive")

    override suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingError> =
      error("Must not unarchive")

    override suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingError> =
      error("Must not delete")
  }

  companion object {
    val initialConfig = ApiConfig("https://linkding.example", "secret")
    val draft = SaveBookmark("https://example.com/article", title = "Article")
    val remote = LinkdingBookmark(id = 42, url = draft.url)
  }
}

internal suspend fun DefaultPendingBookmarks.observeFirst() = get(1) ?: error("No pending item")
