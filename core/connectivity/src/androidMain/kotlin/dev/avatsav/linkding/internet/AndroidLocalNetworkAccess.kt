package dev.avatsav.linkding.internet

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidLocalNetworkAccess(
  private val application: Application,
  private val dispatchers: AppCoroutineDispatchers,
) : LocalNetworkAccess() {
  override val permissionRequired: Boolean
    get() =
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN &&
        application.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.CINNAMON_BUN

  override val permissionGranted = MutableStateFlow(hasPermission())

  override fun refresh() {
    permissionGranted.value = hasPermission()
  }

  private fun hasPermission(): Boolean =
    !permissionRequired ||
      ContextCompat.checkSelfPermission(
        application,
        Manifest.permission.ACCESS_LOCAL_NETWORK,
      ) == PackageManager.PERMISSION_GRANTED

  override suspend fun isLocalServer(hostUrl: String): Boolean =
    withContext(dispatchers.io) {
      val host =
        try {
          URI(hostUrl).host
        } catch (_: URISyntaxException) {
          null
        } ?: return@withContext false
      // mDNS itself can require permission, so do not try resolving .local first.
      if (host.endsWith(".local", ignoreCase = true)) return@withContext true
      try {
        InetAddress.getAllByName(host).any { isLocalNetworkAddress(it.address) }
      } catch (_: UnknownHostException) {
        // Let the normal connection validation report DNS failures, not a permission error.
        false
      }
    }
}
