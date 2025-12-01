package dev.avatsav.linkding.di

object IosGraphHolder {
  fun addComponent(component: Any) {
    GraphHolder.components += component
  }
}
