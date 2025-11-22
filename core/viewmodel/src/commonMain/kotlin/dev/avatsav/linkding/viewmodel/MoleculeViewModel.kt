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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Common interface for managing state and event-driven behavior using Molecule.
 *
 * @param Event Events that can be sent to update state
 * @param Model The UI state model exposed as a [StateFlow]
 */
interface Presentable<Event, Model> {
  val models: StateFlow<Model>

  fun eventSink(event: Event)
}

/**
 * Lightweight presenter for managing state and event-driven behavior using Molecule. Designed to be
 * composed within ViewModels or other presenters.
 *
 * Use [presenterScope] for launching coroutines and [ObserveEvents] to handle UI events.
 *
 * Example:
 * ```kotlin
 * @AssistedInject
 * class BookmarksPresenter(
 *   @Assisted coroutineScope: CoroutineScope,
 *   private val observeBookmarks: ObserveBookmarks,
 * ) : MoleculePresenter<BookmarksEvent, BookmarksState>(coroutineScope) {
 *
 *   @Composable
 *   override fun models(events: Flow<BookmarksEvent>): BookmarksState {
 *     val bookmarks by observeBookmarks.flow.collectAsState(initial = emptyList())
 *
 *     ObserveEvents { event ->
 *       when (event) {
 *         is BookmarksEvent.Refresh -> presenterScope.launch { /* ... */ }
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
 * @param scope The coroutine scope (typically from a parent ViewModel)
 * @param Event UI events to handle
 * @param Model The UI state model
 */
abstract class MoleculePresenter<Event, Model>(scope: CoroutineScope) : Presentable<Event, Model> {

  val presenterScope = scope
  private val moleculeScope = CoroutineScope(scope.coroutineContext + UiDispatcherContext)

  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

  override val models: StateFlow<Model> by
    lazy(LazyThreadSafetyMode.NONE) {
      moleculeScope.launchMolecule(mode = ContextClock) { models(events) }
    }

  override fun eventSink(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
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
 * ViewModel wrapper that delegates state and event management to a [MoleculePresenter]. Combines
 * Jetpack ViewModel lifecycle with Molecule's reactive state management.
 *
 * Subclasses inject a presenter factory and lazily create the presenter with [viewModelScope].
 *
 * Example:
 * ```kotlin
 * @Inject
 * class BookmarksViewModel(
 *   bookmarksPresenterFactory: BookmarksPresenter.Factory
 * ) : MoleculeViewModel<BookmarksEvent, BookmarksState>() {
 *   override val presenter by lazy {
 *     bookmarksPresenterFactory.create(viewModelScope)
 *   }
 * }
 * ```
 *
 * @param Event UI events to handle
 * @param Model The UI state model
 */
abstract class MoleculeViewModel<Event, Model> : ViewModel(), Presentable<Event, Model> {

  /**
   * The presenter that handles all state and event logic. Initialize lazily with [viewModelScope]
   * from an injected factory.
   */
  protected abstract val presenter: MoleculePresenter<Event, Model>

  override val models: StateFlow<Model>
    get() = presenter.models

  override fun eventSink(event: Event) = presenter.eventSink(event)
}
