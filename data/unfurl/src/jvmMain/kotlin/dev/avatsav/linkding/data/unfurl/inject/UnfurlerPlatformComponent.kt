package dev.avatsav.linkding.data.unfurl.inject

import dev.avatsav.linkding.data.unfurl.Unfurler
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

actual interface UnfurlerPlatformComponent {

    @Provides
    @AppScope
    fun provideLinkUnfurler(): Unfurler {
        return Unfurler(me.saket.unfurl.Unfurler())
    }
}
