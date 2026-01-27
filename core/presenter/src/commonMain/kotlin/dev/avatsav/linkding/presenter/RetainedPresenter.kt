package dev.avatsav.linkding.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain

/** Retains a presenter and closes it when retired */
@Composable
fun <P : MoleculePresenter<*, *, *>> retainedPresenter(presenter: P): P {
  return retain { RetainedPresenterObserver(presenter) }.value
}

/** Retains a presenter and closes it when retired */
@Composable
fun <P : MoleculePresenter<*, *, *>> retainedPresenter(vararg keys: Any?, presenter: P): P {
  return retain(keys = keys) { RetainedPresenterObserver(presenter) }.value
}

private class RetainedPresenterObserver<P : MoleculePresenter<*, *, *>>(val value: P) :
  RetainObserver {
  override fun onRetained() {}

  override fun onEnteredComposition() {}

  override fun onExitedComposition() {}

  override fun onRetired() {
    value.close()
  }

  override fun onUnused() {}
}
