package dev.avatsav.linkding

import dev.avatsav.linkding.data.model.ApiConfig
import me.tatarka.inject.annotations.Inject

@Inject
class AndroidUserComponentFactory(private val activityComponent: AndroidUiComponent) : UserComponentFactory {
    override fun create(apiConfig: ApiConfig): SharedUserComponent =
        AndroidUserComponent.create(apiConfig, activityComponent)
}
