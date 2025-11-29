package dev.avatsav.linkding.android

import android.app.Activity
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.avatsav.linkding.android.di.AndroidAppGraph
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.AndroidUiComponent
import dev.avatsav.linkding.di.ComponentHolder
import dev.avatsav.linkding.di.activity.ActivityKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(private val viewModelProviderFactory: ViewModelProvider.Factory) :
  ComponentActivity() {

  override val defaultViewModelProviderFactory: ViewModelProvider.Factory
    get() = viewModelProviderFactory

  @Suppress("UnusedPrivateProperty")
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    val launchMode = getLaunchMode()

    val appComponent: AndroidAppGraph = ComponentHolder.component()
    val uiComponent =
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
      uiComponent.appUi.Content(
        launchMode = launchMode,
        onOpenUrl = { launchUrl(it) },
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
