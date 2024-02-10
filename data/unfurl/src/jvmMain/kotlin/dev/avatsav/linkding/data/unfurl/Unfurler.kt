package dev.avatsav.linkding.data.unfurl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.UnfurlData

actual class Unfurler(private val unfurler: me.saket.unfurl.Unfurler) {

    actual suspend fun unfurl(url: String): Result<UnfurlData, String> {
        return try {
            val result = unfurler.unfurl(url)
            Ok(UnfurlData(url, result?.title, result?.description))
        } catch (e: Throwable) {
            Err(e.message ?: "Error fetching link metadata")
        }
    }
}
