package dev.avatsav.linkding

import dev.avatsav.linkding.api.inject.LinkdingApiPlatformComponent
import dev.avatsav.linkding.data.model.ApiConfiguration
import dev.avatsav.linkding.inject.LinkdingScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@LinkdingScope
@Component
abstract class LinkdingComponent(
    private val apiConfiguration: ApiConfiguration.Linkding,
    @Component val appComponent: SharedAppComponent,
) : LinkdingApiPlatformComponent {

    @LinkdingScope
    @Provides
    fun provideApiConfiguration(): ApiConfiguration.Linkding = apiConfiguration

    companion object

}
