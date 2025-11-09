package dev.avatsav.linkding.di

import dev.zacsweers.metro.createGraph

object IosComponentCreator {
  fun createAppComponent(): IosAppComponent = createGraph<IosAppComponent>()

  fun createUiComponent(appComponent: IosAppComponent): IosUiComponent =
    (appComponent as IosUiComponent.Factory).createUiComponent()
}
