package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.bookmarks.api.interactors.AddBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.CheckBookmarkUrl
import dev.avatsav.linkding.bookmarks.api.interactors.GetBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UpdateBookmark
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Save
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.UrlChanged
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.CheckUrlResult
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import dev.avatsav.linkding.data.model.UpdateBookmark as UpdateBookmarkModel
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val UrlCheckDelay = 500.milliseconds

@AssistedInject
class AddBookmarkPresenter(
  @Assisted route: Route.AddBookmark,
  private val addBookmark: AddBookmark,
  private val getBookmark: GetBookmark,
  private val updateBookmark: UpdateBookmark,
  private val checkBookmarkUrl: CheckBookmarkUrl,
  private val pendingBookmarks: PendingBookmarks,
) : MoleculePresenter<AddBookmarkUiEvent, AddBookmarkUiState, AddBookmarkUiEffect>() {

  private val mode: BookmarkMode = route.toBookmarkMode()

  @Composable
  override fun models(events: Flow<AddBookmarkUiEvent>): AddBookmarkUiState {
    var loadedBookmark: Bookmark? by remember { mutableStateOf(null) }
    var loadingBookmark by remember { mutableStateOf(mode is BookmarkMode.Pending || mode is Edit) }
    var pendingBookmark: PendingBookmark? by remember { mutableStateOf(null) }
    var pendingEditRejected by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf((mode as? BookmarkMode.Shared)?.url.orEmpty()) }
    var errorMessage by remember { mutableStateOf("") }

    val checkingUrl by checkBookmarkUrl.inProgress.collectAsState(false)
    val currentCheck = rememberUrlCheck(url, saving) { checkedUrl -> url == checkedUrl && !saving }
    val existingBookmark = loadedBookmark ?: currentCheck?.result?.existingBookmark
    val pendingUnavailable = pendingEditRejected || isMissingDraft(loadingBookmark, pendingBookmark)

    // Load initial data based on mode
    LaunchedEffect(mode) {
      when (mode) {
        BookmarkMode.New,
        is BookmarkMode.Shared -> Unit
        is BookmarkMode.Pending -> {
          pendingBookmarks.observe(mode.pendingId).collect {
            pendingBookmark = it
            pendingEditRejected = false
            loadingBookmark = false
          }
        }
        is Edit -> {
          getBookmark(mode.bookmarkId)
            .onOk { loadedBookmark = it }
            .onErr {
              Logger.e { "Failed to load bookmark: $it" }
              errorMessage = it.message
            }
          loadingBookmark = false
        }
      }
    }

    ObserveEvents { event ->
      when (event) {
        Close -> emitEffect(AddBookmarkUiEffect.NavigateUp)
        is Save ->
          if (
            !saving && !loadingBookmark && !pendingUnavailable && pendingBookmark?.canEdit != false
          ) {
            saving = true
            val serverId =
              (mode as? BookmarkMode.Edit)?.bookmarkId ?: currentCheck?.targetFor(event.url)
            presenterScope.launch {
              try {
                save(event, serverId, pendingBookmark)
                  .onOk { saved ->
                    if (saved) emitEffect(AddBookmarkUiEffect.BookmarkSaved)
                    else pendingEditRejected = true
                  }
                  .onErr { errorMessage = it.message }
              } finally {
                saving = false
              }
            }
          }

        is UrlChanged -> url = event.url
      }
    }

    return AddBookmarkUiState(
      mode = mode,
      existingBookmark = existingBookmark,
      pendingBookmark = pendingBookmark,
      pendingUnavailable = pendingUnavailable,
      loading = loadingBookmark,
      checkingUrl = checkingUrl,
      checkUrlResult = currentCheck?.result,
      checkedUrl = currentCheck?.url,
      saving = saving,
      errorMessage = errorMessage.ifBlank { null },
    )
  }

  private fun isMissingDraft(loading: Boolean, pending: PendingBookmark?): Boolean =
    mode is BookmarkMode.Pending && !loading && pending == null

  @Composable
  private fun rememberUrlCheck(
    lookupUrl: String,
    saving: Boolean,
    isCurrent: (String) -> Boolean,
  ): CheckedUrl? {
    val stillCurrent by rememberUpdatedState(isCurrent)
    var checked by remember(lookupUrl) { mutableStateOf<CheckedUrl?>(null) }
    LaunchedEffect(lookupUrl, saving) {
      if (mode is Edit || mode is BookmarkMode.Pending || saving || lookupUrl.isBlank()) {
        return@LaunchedEffect
      }
      if (checked != null) return@LaunchedEffect
      delay(UrlCheckDelay)
      checkBookmarkUrl(lookupUrl)
        .onOk { result ->
          if (stillCurrent(lookupUrl)) {
            checked = CheckedUrl(lookupUrl, result)
            if (result.existingBookmark != null) {
              emitEffect(AddBookmarkUiEffect.ExistingBookmarkFound)
            }
          }
        }
        .onErr { Logger.e { "CheckError: $it" } }
    }
    return checked
  }

  private suspend fun save(
    event: Save,
    serverId: Long?,
    pending: PendingBookmark?,
  ): Result<Boolean, BookmarkError> {
    val fields =
      (pending?.bookmark ?: SaveBookmark(event.url)).copy(
        url = event.url,
        title = event.title,
        description = event.description,
        notes = event.notes,
        tags = event.tags.toSet(),
      )
    return when {
      mode is BookmarkMode.Pending -> Ok(pendingBookmarks.edit(mode.pendingId, fields))
      serverId != null ->
        updateBookmark(
            UpdateBookmarkModel(
              id = serverId,
              title = event.title,
              description = event.description,
              notes = event.notes,
              tags = event.tags.toSet(),
            )
          )
          .map { true }
      else -> addBookmark(fields).map { true }
    }
  }

  private data class CheckedUrl(val url: String, val result: CheckUrlResult) {
    fun targetFor(submittedUrl: String): Long? =
      result.existingBookmark?.takeIf { submittedUrl == url || submittedUrl == it.url }?.id
  }

  @AssistedFactory
  interface Factory {
    fun create(route: Route.AddBookmark): AddBookmarkPresenter
  }

  private fun Route.AddBookmark.toBookmarkMode(): BookmarkMode =
    when (this) {
      is New -> BookmarkMode.New
      is Shared -> BookmarkMode.Shared(url)
      is Edit -> BookmarkMode.Edit(bookmarkId)
      is Pending -> BookmarkMode.Pending(pendingId)
    }
}
