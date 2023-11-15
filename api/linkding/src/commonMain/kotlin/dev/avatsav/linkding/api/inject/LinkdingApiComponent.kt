package dev.avatsav.linkding.api.inject

import dev.avatsav.linkding.api.Linkding
import me.tatarka.inject.annotations.Provides

expect interface LinkdingApiPlatformComponent


// TODO: Scoping
interface LinkdingApiComponent {

    @Provides
    fun provideBookmarksApi(linkding: Linkding) = linkding.bookmarks

    @Provides
    fun provideTagsApi(linkding: Linkding) = linkding.tags

}
