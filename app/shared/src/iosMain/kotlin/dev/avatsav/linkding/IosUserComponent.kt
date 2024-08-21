package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UserScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@UserScope
abstract class IosUserComponent(
    @get:Provides val apiConfig: ApiConfig,
    @Component val uiComponent: IosUiComponent,
) : SharedUserComponent {
    companion object
}
