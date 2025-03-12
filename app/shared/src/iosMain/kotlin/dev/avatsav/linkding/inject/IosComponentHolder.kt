package dev.avatsav.linkding.inject

object IosComponentHolder {
  fun addComponent(component: Any) {
    ComponentHolder.components += component
  }
}
