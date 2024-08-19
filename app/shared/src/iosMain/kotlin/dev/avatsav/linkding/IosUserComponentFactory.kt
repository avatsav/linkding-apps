package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig

class IosUserComponentFactory(private val uiComponent: IosUiViewControllerComponent) : UserComponentFactory {
    override fun create(apiConfig: ApiConfig): SharedUserComponent {
        // iOSMain cannot read sources from KSP as they are generated for the each architecture(iOSArm64 etc...)
        // IosUserComponent.create(apiConfig, uiComponent)
        TODO()
    }
}
