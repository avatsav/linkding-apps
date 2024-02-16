package dev.avatsav.linkding

import android.app.Application
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@AppScope
abstract class AndroidAppComponent(
    @get:Provides val application: Application,
) : SharedAppComponent {
    companion object
}
