package dev.avatsav.linkding.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode.ContextClock
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Common interface for managing state, events, and effects using Molecule.
 *
 * @param Event Events that can be sent to update state
 * @param Model The UI state model exposed as a [StateFlow]
 * @param Effect One-time side effects (navigation, toasts, etc.)
 */
interface Presentable<Event, Model, Effect> {
  val models: StateFlow<Model>
  val effects: ReceiveChannel<Effect>

  fun eventSink(event: Event)
}

/**
 * Lightweight presenter for managing state, events, and effects using Molecule. Designed to be
 * composed within ViewModels or other presenters.
 *
 * Use [presenterScope] for launching coroutines, [ObserveEvents] to handle UI events, and
 * [emitEffect] or [trySendEffect] to emit one-time side effects.
 *
 * Example:
 * ```kotlin
 * // Define effects for the feature
 * sealed interface BookmarksEffect {
 *   data object NavigateToAdd : BookmarksEffect
 *   data class ShowToast(val message: String) : BookmarksEffect
 * }
 *
 * @AssistedInject
 * class BookmarksPresenter(
 *   @Assisted coroutineScope: CoroutineScope,
 *   private val observeBookmarks: ObserveBookmarks,
 * ) : MoleculePresenter<BookmarksEvent, BookmarksState, BookmarksEffect>(coroutineScope) {
 *
 *   @Composable
 *   override fun models(events: Flow<BookmarksEvent>): BookmarksState {
 *     val bookmarks by observeBookmarks.flow.collectAsState(initial = emptyList())
 *
 *     ObserveEvents { event ->
 *       when (event) {
 *         BookmarksEvent.AddBookmark -> trySendEffect(BookmarksEffect.NavigateToAdd)
 *         is BookmarksEvent.Delete -> presenterScope.launch {
 *           deleteBookmark(event.id)
 *           emitEffect(BookmarksEffect.ShowToast("Deleted"))
 *         }
 *       }
 *     }
 *
 *     return BookmarksState(bookmarks = bookmarks)
 *   }
 *
 *   @AssistedFactory
 *   interface Factory {
 *     fun create(coroutineScope: CoroutineScope): BookmarksPresenter
 *   }
 * }
 * ```
 *
 * Collect effects in UI:
 * ```kotlin
 * LaunchedEffect(Unit) {
 *   for (effect in presenter.effects) {
 *     when (effect) {
 *       BookmarksEffect.NavigateToAdd -> navigator.navigate(AddScreen)
 *       is BookmarksEffect.ShowToast -> showToast(effect.message)
 *     }
 *   }
 * }
 * ```
 *
 * @param scope The coroutine scope (typically from a parent ViewModel)
 * @param Event UI events to handle
 * @param Model The UI state model
 * @param Effect One-time side effects (navigation, toasts, etc.)
 */
abstract class MoleculePresenter<Event, Model, Effect>(scope: CoroutineScope) :
  Presentable<Event, Model, Effect> {

  val presenterScope = scope
  private val moleculeScope = CoroutineScope(scope.coroutineContext + UiDispatcherContext)

  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)
  private val _effects = Channel<Effect>(Channel.BUFFERED)

  override val effects: ReceiveChannel<Effect> = _effects

  override val models: StateFlow<Model> by
    lazy(LazyThreadSafetyMode.NONE) {
      moleculeScope.launchMolecule(mode = ContextClock) { models(events) }
    }

  override fun eventSink(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
  }

  /**
   * Emits a one-time effect. Suspends if the effect buffer is full. Use this when already in a
   * coroutine context.
   */
  protected suspend fun emitEffect(effect: Effect) {
    _effects.send(effect)
  }

  /**
   * Attempts to emit a one-time effect without suspending. Throws if the effect buffer is full. Use
   * this from non-suspending contexts.
   */
  protected fun trySendEffect(effect: Effect) {
    _effects.trySend(effect).getOrThrow()
  }

  @Composable protected abstract fun models(events: Flow<Event>): Model

  /**
   * Observes events from the UI and executes the provided block for each event. Use
   * [presenterScope] to launch coroutines within the block.
   */
  @Composable
  protected fun ObserveEvents(block: CoroutineScope.(Event) -> Unit) {
    val latestBlock by rememberUpdatedState(block)
    LaunchedEffect(Unit) { events.collect { event: Event -> latestBlock(event) } }
  }
}

/**
 * ViewModel wrapper that delegates state, event, and effect management to a [MoleculePresenter].
 * Combines Jetpack ViewModel lifecycle with Molecule's reactive state management.
 *
 * Subclasses inject a presenter factory and lazily create the presenter with [viewModelScope].
 *
 * Example:
 * ```kotlin
 * @Inject
 * class BookmarksViewModel(
 *   bookmarksPresenterFactory: BookmarksPresenter.Factory
 * ) : MoleculeViewModel<BookmarksEvent, BookmarksState, BookmarksEffect>() {
 *   override val presenter by lazy {
 *     bookmarksPresenterFactory.create(viewModelScope)
 *   }
 * }
 * ```
 *
 * @param Event UI events to handle
 * @param Model The UI state model
 * @param Effect One-time side effects (navigation, toasts, etc.)
 */
abstract class MoleculeViewModel<Event, Model, Effect> :
  ViewModel(), Presentable<Event, Model, Effect> {

  /**
   * The presenter that handles all state, event, and effect logic. Initialize lazily with
   * [viewModelScope] from an injected factory.
   */
  protected abstract val presenter: MoleculePresenter<Event, Model, Effect>

  override val models: StateFlow<Model>
    get() = presenter.models

  override val effects: ReceiveChannel<Effect>
    get() = presenter.effects

  override fun eventSink(event: Event) = presenter.eventSink(event)
}
