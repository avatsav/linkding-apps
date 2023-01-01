package dev.avatsav.linkding.android.ui.add

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.AddBookmarkPresenter
import org.koin.androidx.compose.get
import timber.log.Timber

class AddBookmarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val link = when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            }

            else -> null
        }?.trim()

        Timber.e("Link: $link")
        setContent {
            LinkdingTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val presenter: AddBookmarkPresenter = get()
                    if (!link.isNullOrBlank()) {
                        presenter.setLink(link)
                    }
                    AddBookmarkScreen(presenter)
                }
            }
        }
    }

}