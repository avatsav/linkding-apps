package dev.avatsav.linkding.data.bookmarks.mappers

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.data.model.BookmarkError
import dev.avatsav.linkding.data.model.ConfigurationError
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarkErrorMapper {
    fun map(error: LinkdingError) = BookmarkError(error.message)
}

@Inject
class ConfigurationErrorMapper {
    fun map(error: LinkdingError) = when (error) {
        is LinkdingError.Connectivity -> ConfigurationError.InvalidHostname(error.message)
        is LinkdingError.Unauthorized -> ConfigurationError.InvalidApiKey(error.message)
        is LinkdingError.Other -> ConfigurationError.Other(error.message)
    }

}
