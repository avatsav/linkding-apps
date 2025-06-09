package dev.avatsav.linkding.inject

object IosComponentCreator {
  fun createAppComponent(): IosAppComponent = createIosAppComponent()

  fun createUiComponent(appComponent: IosAppComponent): IosUiComponent =
    (appComponent as IosUiComponent.Factory).createUiComponent()
}
