package dev.avatsav.linkding.android

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.AndroidUiComponent
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.inject.ComponentHolder
import dev.avatsav.linkding.ui.SetupScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val sharedLink = getSharedLinkFromIntent()

        val appComponent: AndroidAppComponent = ComponentHolder.component()
        val component = ComponentHolder.component<AndroidUiComponent.Factory>()
            .create(this)
            .also {
                ComponentHolder.components += it
            }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appComponent.appPreferences.observeAppTheme().collect(::enableEdgeToEdge)
            }
        }

        // TODO: Pass the sharedLink to the appUi
        setContent {
            val backstack = rememberSaveableBackStack(root = SetupScreen)
            val navigator = rememberCircuitNavigator(backstack)

            component.appUi.Content(
                backstack,
                navigator,
                { launchUrl(it) },
                Modifier,
            )
        }
    }

    private fun getSharedLinkFromIntent(): String? = when (intent?.action) {
        Intent.ACTION_SEND -> {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        }

        else -> null
    }?.trim()
}

private val customTabsIntent = CustomTabsIntent.Builder().setShowTitle(true)
    .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
    .setShareState(CustomTabsIntent.SHARE_STATE_OFF).build()

private fun MainActivity.launchUrl(url: String): Boolean {
    customTabsIntent.launchUrl(this, Uri.parse(url))
    return true
}

private fun ComponentActivity.enableEdgeToEdge(appTheme: AppTheme) {
    val style = when (appTheme) {
        AppTheme.System -> SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        AppTheme.Light -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        AppTheme.Dark -> SystemBarStyle.dark(Color.TRANSPARENT)
    }
    enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
}
