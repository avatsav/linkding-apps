package dev.avatsav.linkding.data.unfurl.inject

import dev.avatsav.linkding.data.unfurl.Unfurler
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import platform.LinkPresentation.LPMetadataProvider

actual interface UnfurlerPlatformComponent {

    @Provides
    @AppScope
    fun provideLinkUnfurler(): Unfurler {
        return Unfurler(LPMetadataProvider())
    }
}
