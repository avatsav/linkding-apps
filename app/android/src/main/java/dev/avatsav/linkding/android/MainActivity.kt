package dev.avatsav.linkding.android

import android.content.Context
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
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.AndroidActivityComponent
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.create
import dev.avatsav.linkding.ui.RootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val sharedLink = getSharedLinkFromIntent()
        val appComponent = AndroidAppComponent.from(this)
        val activityComponent = AndroidActivityComponent.create(this, appComponent)

        setContent {
            val backstack = rememberSaveableBackStack(root = RootScreen(sharedLink))
            val navigator = rememberCircuitNavigator(backstack)

            activityComponent.appContent(
                backstack,
                navigator,
                { url -> launchUrl(url) },
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

private val customTabsIntent = CustomTabsIntent.Builder()
    .setShowTitle(true)
    .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
    .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    .build()

fun MainActivity.launchUrl(url: String) {
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

private fun AndroidAppComponent.Companion.from(context: Context): AndroidAppComponent {
    return (context.applicationContext as LinkdingApplication).component
}