package dev.avatsav.linkding.android.ui

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent.Builder
import androidx.browser.customtabs.CustomTabsIntent.COLOR_SCHEME_SYSTEM
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_OFF

private val customTabsIntent = Builder()
    .setShowTitle(true)
    .setColorScheme(COLOR_SCHEME_SYSTEM)
    .setShareState(SHARE_STATE_OFF)
    .build()

fun launchUrl(context: Context, url: String) {
    customTabsIntent.launchUrl(context, Uri.parse(url))
}
