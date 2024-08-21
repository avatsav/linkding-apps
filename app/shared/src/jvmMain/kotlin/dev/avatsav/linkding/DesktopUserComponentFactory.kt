package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import me.tatarka.inject.annotations.Inject

@Inject
class DesktopUserComponentFactory(private val desktopUiComponent: DesktopUiComponent) : UserComponentFactory {
    override fun create(apiConfig: ApiConfig): SharedUserComponent =
        DesktopUserComponent.create(apiConfig, desktopUiComponent)
}
