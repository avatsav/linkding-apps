package dev.avatsav.linkding.android.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.AndroidActivityComponent
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.android.LinkdingApplication
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
            val backstack =
                rememberSaveableBackStack { push(RootScreen(sharedLink)) }
            val navigator = rememberCircuitNavigator(backstack)

            activityComponent.appContent(
                backstack,
                navigator,
                { url -> launchUrl(this@MainActivity, url) },
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

private fun AndroidAppComponent.Companion.from(context: Context): AndroidAppComponent {
    return (context.applicationContext as LinkdingApplication).component
}
