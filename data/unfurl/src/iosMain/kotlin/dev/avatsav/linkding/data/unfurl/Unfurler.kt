package dev.avatsav.linkding.data.unfurl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.UnfurlData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import platform.Foundation.NSURL
import platform.LinkPresentation.LPMetadataProvider

actual class Unfurler(private val metadataProvider: LPMetadataProvider) {

    actual suspend fun unfurl(url: String): Result<UnfurlData, String> {
        val nsUrl = NSURL(string = url)
        return suspendCoroutine { continuation ->
            try {
                metadataProvider.startFetchingMetadataForURL(
                    nsUrl,
                    completionHandler = { lpLinkMetadata, nsError ->
                        if (lpLinkMetadata != null) {
                            continuation.resume(
                                Ok(
                                    UnfurlData(
                                        url,
                                        lpLinkMetadata.title,
                                        lpLinkMetadata.description,
                                    ),
                                ),
                            )
                        } else if (nsError != null) {
                            continuation.resume(
                                Err(nsError.description ?: "Error getting link metadata"),
                            )
                        }
                    },
                )
            } catch (e: Throwable) {
                continuation.resume(
                    Err(e.message ?: "Error getting link metadata"),
                )
            }
        }
    }
}
