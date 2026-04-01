package dev.avatsav.linkding.ui.compose

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState

interface SnackbarMessage {
  @Composable fun message(): String

  @Composable fun actionLabel(): String?

  val onAction: (() -> Unit)?
}

@Composable
fun ObserveSnackbar(
  snackbarMessage: SnackbarMessage?,
  snackbarHostState: SnackbarHostState,
  onDismiss: () -> Unit,
) {
  val message = snackbarMessage?.message()
  val actionLabel = snackbarMessage?.actionLabel()
  val currentOnDismiss = rememberUpdatedState(onDismiss)
  val currentOnAction = rememberUpdatedState(snackbarMessage?.onAction)

  LaunchedEffect(snackbarMessage) {
    if (snackbarMessage == null || message == null) return@LaunchedEffect

    val result =
      snackbarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = SnackbarDuration.Short,
      )

    when (result) {
      SnackbarResult.ActionPerformed -> currentOnAction.value?.invoke()
      SnackbarResult.Dismissed -> currentOnDismiss.value()
    }
  }
}
