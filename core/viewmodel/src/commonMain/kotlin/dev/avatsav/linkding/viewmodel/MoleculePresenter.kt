package dev.avatsav.linkding.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import app.cash.molecule.launchMolecule
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Common interface for managing state, events, and effects using Molecule.
 *
 * @param Event Events that can be sent to update state
 * @param Model The UI state model exposed as a [StateFlow]
 * @param Effect One-time side effects (navigation, toasts, etc.)
 */
interface Presenter<Event, Model, Effect> {
  val models: StateFlow<Model>
  val effects: ReceiveChannel<Effect>

  fun eventSink(event: Event)
}

/**
 * Lightweight presenter for managing state, events, and effects using Molecule.
 *
 * Use [presenterScope] to launch coroutines, [ObserveEvents] to handle events, and [trySendEffect]
 * or [emitEffect] to emit side effects.
 *
 * Example:
 * ```kotlin
 * @AssistedInject
 * class MyPresenter(
 *   @Assisted coroutineScope: CoroutineScope,
 *   private val repository: Repository,
 * ) : MoleculePresenter<MyEvent, MyState, MyEffect>(coroutineScope) {
 *
 *   @Composable
 *   override fun models(events: Flow<MyEvent>): MyState {
 *     var count by remember { mutableStateOf(0) }
 *
 *     ObserveEvents { event ->
 *       when (event) {
 *         Increment -> count++
 *         Save -> presenterScope.launch {
 *           repository.save(count)
 *           emitEffect(MyEffect.Saved)
 *         }
 *       }
 *     }
 *
 *     return MyState(count = count)
 *   }
 *
 *   @AssistedFactory
 *   interface Factory {
 *     fun create(scope: CoroutineScope): MyPresenter
 *   }
 * }
 * ```
 *
 * @param scope Coroutine scope from parent ViewModel
 * @param Event UI events to handle
 * @param Model UI state model
 * @param Effect One-time side effects
 */
abstract class MoleculePresenter<Event, Model, Effect> : Presenter<Event, Model, Effect> {

  val presenterScope = CoroutineScope(PlatformMainDispatcher + SupervisorJob())

  private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

  private val _effects = Channel<Effect>(Channel.BUFFERED)
  override val effects: ReceiveChannel<Effect> = _effects

  override val models: StateFlow<Model> by
    lazy(LazyThreadSafetyMode.NONE) {
      presenterScope.launchMolecule(mode = PlatformRecompositionMode) { models(events) }
    }

  override fun eventSink(event: Event) {
    if (!events.tryEmit(event)) {
      error("Event buffer overflow.")
    }
  }

  /**
   * Emits a side effect. Suspends if buffer is full. Use from suspending contexts (e.g., within
   * `presenterScope.launch`).
   */
  protected fun emitEffect(effect: Effect) {
    presenterScope.launch { _effects.send(effect) }
  }

  @Composable protected abstract fun models(events: Flow<Event>): Model

  /**
   * Observes events from the UI and handles them in the presenter. Use [presenterScope] to launch
   * coroutines.
   */
  @Composable
  protected fun ObserveEvents(block: CoroutineScope.(Event) -> Unit) {
    val latestBlock by rememberUpdatedState(block)
    LaunchedEffect(Unit) { events.collect { event: Event -> latestBlock(event) } }
  }

  fun close() {
    presenterScope.cancel()
  }
}

/**
 * Observes effects and handles them in your UI composable.
 *
 * Example:
 * ```kotlin
 * @Composable
 * fun MyScreen(presenter: MyPresenter) {
 *   val state by presenter.models.collectAsState()
 *
 *   ObserveEffects(presenter.effects) { effect ->
 *     when (effect) {
 *       NavigateUp -> navigator.goBack()
 *       ShowMessage -> showSnackbar("Done")
 *     }
 *   }
 *
 *   // UI content...
 * }
 * ```
 */
@Composable
fun <Effect> ObserveEffects(effects: ReceiveChannel<Effect>, block: suspend (Effect) -> Unit) {
  val latestBlock by rememberUpdatedState(block)
  LaunchedEffect(effects) {
    for (effect in effects) {
      latestBlock(effect)
    }
  }
}

@Composable
fun <P : MoleculePresenter<*, *, *>> retainedPresenter(presenter: P): P {
  return retain {
      object : RetainObserver {
        val value = presenter

        override fun onRetained() {
          Logger.d { "Retained presenter: ${value::class.simpleName}" }
        }

        override fun onEnteredComposition() {}

        override fun onExitedComposition() {}

        override fun onRetired() {
          value.close()
          Logger.d { "Retired presenter: ${value::class.simpleName}" }
        }

        override fun onUnused() {}
      }
    }
    .value
}
