package dev.avatsav.linkding.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.AndroidAppComponent
import dev.avatsav.linkding.di.AndroidUiComponent
import dev.avatsav.linkding.di.ComponentHolder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  @Suppress("UnusedPrivateProperty")
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    val launchMode = getLaunchMode()

    val appComponent: AndroidAppComponent = ComponentHolder.component()
    val component =
      ComponentHolder.component<AndroidUiComponent.Factory>().create().also {
        ComponentHolder.components += it
      }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        appComponent.appPreferences.observeAppTheme().collect(::enableEdgeToEdge)
      }
    }

    // Pass the sharedLink to the appUi
    setContent {
      component.appUi.Content(
        launchMode = launchMode,
        onOpenUrl = { launchUrl(it) },
        onRootPop = backDispatcherRootPop(),
        modifier = Modifier,
      )
    }
  }

  private fun getLaunchMode(): LaunchMode {
    val sharedLink = getSharedLinkFromIntent()
    return if (sharedLink != null) LaunchMode.SharedLink(sharedLink) else LaunchMode.Normal
  }

  private fun getSharedLinkFromIntent(): String? =
    intent
      ?.takeIf { it.action == Intent.ACTION_SEND }
      ?.getStringExtra(Intent.EXTRA_TEXT)
      ?.trim()
      ?.takeIf { it.isValidWebUrl() }

  private fun String.isValidWebUrl(): Boolean =
    startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
}

private val customTabsIntent =
  CustomTabsIntent.Builder()
    .setShowTitle(true)
    .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
    .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    .build()

/**
 * https://github.com/slackhq/circuit/blob/158d07b703778816a69f3cb13b63ef456a8c42e9/circuit-foundation/src/androidMain/kotlin/com/slack/circuit/foundation/Navigator.android.kt#L34
 */
@Composable
private fun backDispatcherRootPop(): () -> Unit {
  val onBackPressedDispatcher =
    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
      ?: error("No OnBackPressedDispatcherOwner found, unable to handle root navigation pops.")
  return { onBackPressedDispatcher.onBackPressed() }
}

private fun MainActivity.launchUrl(url: String): Boolean {
  customTabsIntent.launchUrl(this, url.toUri())
  return true
}

private fun ComponentActivity.enableEdgeToEdge(appTheme: AppTheme) {
  val style =
    when (appTheme) {
      AppTheme.System -> SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
      AppTheme.Light -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
      AppTheme.Dark -> SystemBarStyle.dark(Color.TRANSPARENT)
    }
  enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
}
