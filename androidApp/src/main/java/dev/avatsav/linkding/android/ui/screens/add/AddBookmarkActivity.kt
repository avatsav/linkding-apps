package dev.avatsav.linkding.android.ui.screens.add

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

class AddBookmarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedLink = when (intent?.action) {
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            }

            else -> null
        }?.trim()
        val finishActivity: () -> Unit = {
            finishAndRemoveTask()
        }
        setContent {
            AddBookmarksScreenFromActivity(sharedUrl = sharedLink, finishActivity = finishActivity)
        }
    }
}

@Composable
private fun AddBookmarksScreenFromActivity(
    sharedUrl: String?,
    finishActivity: () -> Unit
) {
    LinkdingTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddBookmarkScreen(
                sharedUrl = sharedUrl,
                onBookmarkSaved = {
                    finishActivity()
                },
                onClose = {
                    finishActivity()
                }
            )
        }
    }
}