package dev.avatsav.linkding.data.unfurl

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.data.model.UnfurlData

expect class Unfurler {

    suspend fun unfurl(url: String): Result<UnfurlData, String>
}
