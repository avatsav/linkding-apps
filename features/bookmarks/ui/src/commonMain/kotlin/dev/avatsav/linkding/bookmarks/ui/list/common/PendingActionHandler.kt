package dev.avatsav.linkding.bookmarks.ui.list.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.data.model.Bookmark
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal sealed interface PendingAction {
  val bookmarkId: Long
  val bookmark: Bookmark
  val timestamp: Long

  data class Delete(
    override val bookmarkId: Long,
    override val bookmark: Bookmark,
    override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
  ) : PendingAction

  data class Archive(
    override val bookmarkId: Long,
    override val bookmark: Bookmark,
    override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
  ) : PendingAction

  data class Unarchive(
    override val bookmarkId: Long,
    override val bookmark: Bookmark,
    override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
  ) : PendingAction
}

internal data class PendingActionHandlerState(
  val pendingIds: Set<Long>,
  val snackbarMessage: SnackbarMessage?,
  val scheduleAction: (PendingAction) -> Unit,
  val undoAction: () -> Unit,
  val dismissSnackbar: () -> Unit,
  val clearPendingIds: () -> Unit,
)

@Composable
internal fun rememberPendingBookmarkActionHandler(
  scope: CoroutineScope,
  deleteBookmark: DeleteBookmark,
  archiveBookmark: ArchiveBookmark,
  unarchiveBookmark: UnarchiveBookmark,
  removePendingIdsOnSuccess: () -> Boolean = { false },
  onSuccess: (PendingAction) -> Unit = {},
  onFailure: (PendingAction) -> Unit = {},
): PendingActionHandlerState {

  val pendingIds = remember { mutableStateSetOf<Long>() }
  var currentPendingAction by remember { mutableStateOf<PendingActionState?>(null) }
  var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

  lateinit var scheduleAction: (PendingAction) -> Unit
  lateinit var undoCurrentAction: () -> Unit

  suspend fun executeActionInternal(action: PendingAction) {
    val result =
      when (action) {
        is PendingAction.Delete -> deleteBookmark(action.bookmarkId)
        is PendingAction.Archive -> archiveBookmark(action.bookmarkId)
        is PendingAction.Unarchive -> unarchiveBookmark(action.bookmarkId)
      }

    result
      .onSuccess {
        if (removePendingIdsOnSuccess()) {
          pendingIds.remove(action.bookmarkId)
        }
        onSuccess(action)
      }
      .onFailure { error ->
        pendingIds.remove(action.bookmarkId)

        snackbarMessage =
          SnackbarMessage(
            message = "Action failed: ${error.message}",
            actionLabel = "Retry",
            onAction = { scheduleAction(action) },
          )

        onFailure(action)
      }
  }

  undoCurrentAction = {
    currentPendingAction?.let { pendingState ->
      pendingState.job.cancel()
      pendingIds.remove(pendingState.action.bookmarkId)
      currentPendingAction = null
      snackbarMessage = null
    }
  }

  scheduleAction = { action ->
    // If there's already a pending action, execute it immediately
    currentPendingAction?.let { existing ->
      existing.job.cancel()
      scope.launch { executeActionInternal(existing.action) }
    }

    pendingIds.add(action.bookmarkId)

    val job =
      scope.launch {
        delay(UndoActionDelayMillis)
        executeActionInternal(action)
        currentPendingAction = null // Clear after execution
      }

    currentPendingAction = PendingActionState(action, job)

    snackbarMessage =
      SnackbarMessage(
        message =
          when (action) {
            is PendingAction.Delete -> "Bookmark deleted"
            is PendingAction.Archive -> "Bookmark archived"
            is PendingAction.Unarchive -> "Bookmark unarchived"
          },
        actionLabel = "Undo",
        onAction = undoCurrentAction,
      )
  }

  return PendingActionHandlerState(
    pendingIds = pendingIds,
    snackbarMessage = snackbarMessage,
    scheduleAction = scheduleAction,
    undoAction = undoCurrentAction,
    dismissSnackbar = { snackbarMessage = null },
    clearPendingIds = { pendingIds.clear() },
  )
}

private data class PendingActionState(val action: PendingAction, val job: Job)

private const val UndoActionDelayMillis = 4000L
