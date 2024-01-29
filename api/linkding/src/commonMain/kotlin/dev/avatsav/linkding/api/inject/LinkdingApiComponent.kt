package dev.avatsav.linkding.api.inject

import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.inject.LinkdingScope
import me.tatarka.inject.annotations.Provides

expect interface LinkdingApiPlatformComponent

interface LinkdingApiComponent {

    @LinkdingScope
    @Provides
    fun provideBookmarksApi(linkding: Linkding) = linkding.bookmarks

    @LinkdingScope
    @Provides
    fun provideTagsApi(linkding: Linkding) = linkding.tags
}
