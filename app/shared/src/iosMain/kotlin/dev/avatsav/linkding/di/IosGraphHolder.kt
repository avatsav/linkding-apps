package dev.avatsav.linkding.di

object IosGraphHolder {
  fun addGraph(graph: Any) {
    GraphHolder.graphs += graph
  }
}
