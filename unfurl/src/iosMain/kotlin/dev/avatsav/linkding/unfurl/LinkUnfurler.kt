package dev.avatsav.linkding.unfurl

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import platform.Foundation.NSURL
import platform.LinkPresentation.LPMetadataProvider

actual class LinkUnfurler(private val metadataProvider: LPMetadataProvider) {

    actual suspend fun unfurl(url: String): UnfurlResult {
        val nsUrl = NSURL(string = url)
        return suspendCoroutine { continuation ->
            try {
                metadataProvider.startFetchingMetadataForURL(
                    nsUrl,
                    completionHandler = { lpLinkMetadata, nsError ->
                        if (lpLinkMetadata != null) {
                            continuation.resume(
                                UnfurlResult.Data(
                                    url,
                                    lpLinkMetadata.title,
                                    lpLinkMetadata.description,
                                ),
                            )
                        } else if (nsError != null) {
                            continuation.resume(
                                UnfurlResult.Error(
                                    nsError.description ?: "Error getting link metadata",
                                ),
                            )
                        }
                    },
                )
            } catch (e: Throwable) {
                continuation.resume(
                    UnfurlResult.Error(
                        e.message ?: "Error getting link metadata",
                    ),
                )
            }
        }
    }
}
