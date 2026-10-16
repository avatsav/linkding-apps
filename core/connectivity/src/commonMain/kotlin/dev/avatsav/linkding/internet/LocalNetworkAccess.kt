package dev.avatsav.linkding.internet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/** Local-server access policy. Checking access never opens a permission dialog. */
abstract class LocalNetworkAccess {
  enum class State {
    NotRequired,
    Granted,
    Required,
  }

  protected abstract val permissionRequired: Boolean
  protected abstract val permissionGranted: StateFlow<Boolean>

  protected abstract suspend fun isLocalServer(hostUrl: String): Boolean

  fun observe(hostUrl: String): Flow<State> = flow {
    if (!permissionRequired || !isLocalServer(hostUrl)) {
      emit(State.NotRequired)
    } else {
      emitAll(
        permissionGranted.map { if (it) State.Granted else State.Required }.distinctUntilChanged()
      )
    }
  }

  suspend fun check(hostUrl: String): State {
    refresh()
    return observe(hostUrl).first()
  }

  /** Refresh after a permission result or returning from system settings. */
  abstract fun refresh()
}

open class UnrestrictedLocalNetworkAccess : LocalNetworkAccess() {
  override val permissionRequired = false
  override val permissionGranted = kotlinx.coroutines.flow.MutableStateFlow(true)

  override suspend fun isLocalServer(hostUrl: String) = false

  override fun refresh() = Unit
}

/** Loopback is deliberately excluded: Android's LAN restriction does not cover it. */
@Suppress("MagicNumber")
internal fun isLocalNetworkAddress(address: ByteArray): Boolean {
  val bytes = address.map { it.toInt() and 0xff }
  return when (bytes.size) {
    4 ->
      bytes[0] == 10 ||
        (bytes[0] == 172 && bytes[1] in 16..31) ||
        (bytes[0] == 192 && bytes[1] == 168) ||
        (bytes[0] == 169 && bytes[1] == 254) ||
        bytes[0] in 224..239
    16 ->
      (bytes[0] and 0xfe) == 0xfc ||
        (bytes[0] == 0xfe && (bytes[1] and 0xc0) == 0x80) ||
        bytes[0] == 0xff
    else -> false
  }
}
