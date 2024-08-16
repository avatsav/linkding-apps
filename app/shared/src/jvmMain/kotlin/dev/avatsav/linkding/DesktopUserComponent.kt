package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UserScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@UserScope
abstract class DesktopUserComponent(
    @get:Provides val apiConfig: ApiConfig,
    @Component uiComponent: DesktopUiComponent,
) {
    companion object
}
