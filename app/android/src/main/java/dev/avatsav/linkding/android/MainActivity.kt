package dev.avatsav.linkding.android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.domain.models.LaunchMode
import dev.avatsav.linkding.inject.AndroidAppComponent
import dev.avatsav.linkding.inject.AndroidUiComponent
import dev.avatsav.linkding.inject.ComponentHolder
import dev.avatsav.linkding.ui.AuthScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  @Suppress("UnusedPrivateProperty")
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    val launchMode = getLaunchMode()

    val appComponent: AndroidAppComponent = ComponentHolder.component()
    val component =
      ComponentHolder.component<AndroidUiComponent.Factory>().create(this).also {
        ComponentHolder.components += it
      }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        appComponent.appPreferences.observeAppTheme().collect(::enableEdgeToEdge)
      }
    }

    // Pass the sharedLink to the appUi
    setContent {
      val backstack = rememberSaveableBackStack(root = AuthScreen)
      val navigator = rememberCircuitNavigator(backstack)

      component.appUi.Content(launchMode, backstack, navigator, { launchUrl(it) }, Modifier)
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
