package dev.avatsav.linkding.di

object IosComponentHolder {
  fun addComponent(component: Any) {
    ComponentHolder.components += component
  }
}
