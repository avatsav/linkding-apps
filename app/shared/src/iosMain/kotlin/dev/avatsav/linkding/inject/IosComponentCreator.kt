package dev.avatsav.linkding.inject

import dev.avatsav.linkding.IosAppComponent
import dev.avatsav.linkding.IosUiComponent
import dev.avatsav.linkding.createIosAppComponent

object IosComponentCreator {
    fun createAppComponent(): IosAppComponent = createIosAppComponent()

    fun createUiComponent(appComponent: IosAppComponent): IosUiComponent = (appComponent as IosUiComponent.Factory).createUiComponent()
}
