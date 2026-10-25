package dev.avatsav.linkding.ui.compose

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

@Composable
actual fun rememberLocalNetworkPermissionRequest(
  onResult: (Boolean) -> Unit
): LocalNetworkPermissionRequest {
  val activity = checkNotNull(LocalActivity.current)
  val latestOnResult by rememberUpdatedState(onResult)
  val preferences =
    remember(activity) {
      activity.getSharedPreferences("local_network_permission", Context.MODE_PRIVATE)
    }
  fun hasPermission() =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.CINNAMON_BUN ||
      ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_LOCAL_NETWORK) ==
        PackageManager.PERMISSION_GRANTED
  fun shouldOpenSettings() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN &&
      !hasPermission() &&
      preferences.getBoolean("requested", false) &&
      !ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.ACCESS_LOCAL_NETWORK,
      )
  var opensSettings by remember(activity) { mutableStateOf(shouldOpenSettings()) }
  val settingsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      opensSettings = shouldOpenSettings()
      latestOnResult(hasPermission())
    }
  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      opensSettings = shouldOpenSettings()
      latestOnResult(granted)
    }
  return LocalNetworkPermissionRequest(opensSettings = opensSettings) {
    when {
      hasPermission() -> latestOnResult(true)
      shouldOpenSettings() ->
        settingsLauncher.launch(
          Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            "package:${activity.packageName}".toUri(),
          )
        )
      else -> {
        preferences.edit().putBoolean("requested", true).apply()
        permissionLauncher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
      }
    }
  }
}
