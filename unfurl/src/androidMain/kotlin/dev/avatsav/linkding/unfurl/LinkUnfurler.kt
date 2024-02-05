package dev.avatsav.linkding.unfurl

import me.saket.unfurl.Unfurler

actual class LinkUnfurler(private val unfurler: Unfurler) {

    actual suspend fun unfurl(url: String): UnfurlResult {
        return try {
            val result = unfurler.unfurl(url)
            UnfurlResult.Data(url, result?.title, result?.description)
        } catch (e: Throwable) {
            UnfurlResult.Error(e.message ?: "Error getting link metadata")
        }
    }
}
