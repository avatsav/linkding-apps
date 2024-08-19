package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig

class AndroidUserComponentFactory(private val activityComponent: AndroidActivityComponent) : UserComponentFactory {
    override fun create(apiConfig: ApiConfig): SharedUserComponent =
        AndroidUserComponent.create(apiConfig, activityComponent)
}
