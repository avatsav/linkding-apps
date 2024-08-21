package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import me.tatarka.inject.annotations.Inject

/**
 * If you're editing this file, make sure you mirror the changes in iosArm64Main
 */
@Inject
actual class IosUserComponentFactory(private val uiComponent: IosUiComponent) : UserComponentFactory {
    override fun create(apiConfig: ApiConfig): SharedUserComponent =
        IosUserComponent.create(apiConfig, uiComponent)
}
