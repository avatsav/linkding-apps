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
 * An abstract class for managing state and event-driven behavior in a Jetpack Compose-based
 * ViewModel. `MoleculeViewModel` leverages the `Molecule` library for achieving unidirectional data
 * flow by combining `StateFlow` for state management and `Flow` for event handling. It provides a
 * structured approach to handle UI state and events in a composable-driven architecture.
 *
 * @param Event A generic type representing events that the UI can send to the `MoleculeViewModel`.
 * @param Model A generic type representing the UI state model exposed by the `MoleculeViewModel`.
 */
abstract class MoleculeViewModel<Event, Model> : ViewModel() {

  internal val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + UiDispatcherContext)

  /**
   * Events have a capacity large enough to handle simultaneous UI events, but small enough to
   * surface issues if they get backed up for some reason.
   */
  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

  val models: StateFlow<Model> by
    lazy(LazyThreadSafetyMode.NONE) {
      moleculeScope.launchMolecule(mode = ContextClock) { models(events) }
    }

  /**
   * Sends an event to the internal event stream. Use this method to propagate events into the
   * `MoleculeViewModel` for processing.
   *
   * If the internal event buffer is full and unable to emit the new event, an error will be thrown.
   *
   * @param event The event object to be sent to the internal event stream. This event is expected
   *   to be handled by the `models` implementation or other consumers of the event flow.
   */
  fun eventSink(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
  }

  @Composable protected abstract fun models(events: Flow<Event>): Model

  /**
   * Helper function for collecting events from a [MoleculeViewModel].
   *
   * Collects events emitted by the `events` flow and executes the provided `block` for each event.
   *
   * @param block A function that defines the action to perform for each collected event. This
   *   function is executed within the context of a `CoroutineScope` and receives an `Event` as a
   *   parameter.
   */
  @Composable
  fun ObserveEvents(block: CoroutineScope.(Event) -> Unit) {
    val latestBlock by rememberUpdatedState(block)
    LaunchedEffect(Unit) { events.collect { event: Event -> latestBlock(event) } }
  }
}

/**
 * A lightweight presenter for managing state and event-driven behavior using Molecule. Unlike
 * `MoleculeViewModel`, this does not extend `ViewModel` and can be easily composed within other
 * ViewModels by accepting a parent scope.
 *
 * Use this for child presenters that need to be coordinated by a parent ViewModel.
 *
 * @param scope The coroutine scope to use (typically from a parent ViewModel)
 * @param Event A generic type representing events that the UI can send to the presenter
 * @param Model A generic type representing the UI state model exposed by the presenter
 */
abstract class MoleculePresenter<Event, Model>(scope: CoroutineScope) {

  val presenterScope = scope
  private val moleculeScope = CoroutineScope(scope.coroutineContext + UiDispatcherContext)

  /**
   * Events have a capacity large enough to handle simultaneous UI events, but small enough to
   * surface issues if they get backed up for some reason.
   */
  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

  val models: StateFlow<Model> by
    lazy(LazyThreadSafetyMode.NONE) {
      moleculeScope.launchMolecule(mode = ContextClock) { models(events) }
    }

  /**
   * Sends an event to the internal event stream.
   *
   * If the internal event buffer is full and unable to emit the new event, an error will be thrown.
   */
  fun eventSink(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
  }

  @Composable protected abstract fun models(events: Flow<Event>): Model

  /**
   * Helper function for observing events from a [MoleculePresenter].
   *
   * Collects events emitted by the `events` flow and executes the provided `block` for each event.
   */
  @Composable
  protected fun ObserveEvents(block: CoroutineScope.(Event) -> Unit) {
    val latestBlock by rememberUpdatedState(block)
    LaunchedEffect(Unit) { events.collect { event: Event -> latestBlock(event) } }
  }
}
