package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/** Form fields and the Save intent that must survive a trip to Android app settings. */
internal class AuthFormState(
  hostUrl: String = "",
  apiKey: String = "",
  private var permissionSavePending: Boolean = false,
) {
  var hostUrl by mutableStateOf(hostUrl)
  var apiKey by mutableStateOf(apiKey)

  fun awaitPermissionResult() {
    permissionSavePending = true
  }

  fun consumePermissionResult(granted: Boolean): AuthUiEvent.SaveCredentials? {
    val resumeSave = granted && permissionSavePending
    permissionSavePending = false
    return if (resumeSave) AuthUiEvent.SaveCredentials(hostUrl, apiKey) else null
  }

  companion object {
    val Saver =
      listSaver<AuthFormState, Any>(
        save = { listOf(it.hostUrl, it.apiKey, it.permissionSavePending) },
        restore = { AuthFormState(it[0] as String, it[1] as String, it[2] as Boolean) },
      )
  }
}

@Composable
internal fun rememberAuthFormState(): AuthFormState =
  rememberSaveable(saver = AuthFormState.Saver) { AuthFormState() }
