package dev.avatsav.linkding.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain

/** Retains a presenter and closes it when retired */
@Composable
inline fun <reified P : MoleculePresenter<*, *, *>> retainPresenter(
  noinline calculation: () -> P
): P {
  return retain { RetainedPresenterObserver(calculation()) }.value
}

/** Retains a presenter and closes it when retired */
@Composable
inline fun <reified P : MoleculePresenter<*, *, *>> retainPresenter(
  vararg keys: Any?,
  noinline calculation: () -> P,
): P {
  return retain(keys = keys) { RetainedPresenterObserver(calculation()) }.value
}

class RetainedPresenterObserver<P : MoleculePresenter<*, *, *>>(val value: P) : RetainObserver {
  override fun onRetained() {}

  override fun onEnteredComposition() {}

  override fun onExitedComposition() {}

  override fun onRetired() {
    value.close()
  }

  override fun onUnused() {}
}
