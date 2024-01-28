package dev.avatsav.linkding.unfurl

expect class LinkUnfurler {

    suspend fun unfurl(url: String): UnfurlResult
}

sealed interface UnfurlResult {
    data class Data(
        val url: String,
        val title: String?,
        val description: String?,
    ) : UnfurlResult

    data class Error(val message: String) : UnfurlResult
}
