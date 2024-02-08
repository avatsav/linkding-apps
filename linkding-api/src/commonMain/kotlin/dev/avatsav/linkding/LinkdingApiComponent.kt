package dev.avatsav.linkding

import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.inject.LinkdingScope
import me.tatarka.inject.annotations.Provides

expect interface LinkdingApiPlatformComponent

interface LinkdingApiComponent : LinkdingApiPlatformComponent {

    @LinkdingScope
    @Provides
    fun provideBookmarksApi(linkding: Linkding) = linkding.bookmarks

    @LinkdingScope
    @Provides
    fun provideTagsApi(linkding: Linkding) = linkding.tags
}
