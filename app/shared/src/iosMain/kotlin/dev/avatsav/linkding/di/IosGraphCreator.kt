package dev.avatsav.linkding.di

import dev.zacsweers.metro.createGraph

object IosGraphCreator {
  fun createAppGraph(): IosAppGraph = createGraph<IosAppGraph>()

  fun createUiGraph(appGraph: IosAppGraph): IosUiGraph =
    (appGraph as IosUiGraph.Factory).createUiGraph()
}
