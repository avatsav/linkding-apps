package dev.avatsav.linkding

import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.inject.qualifier.Authenticated
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent

@SingleIn(UserScope::class)
@ContributesSubcomponent(UserScope::class)
interface UserComponent {

    @Authenticated
    val circuit: Circuit

    @ContributesSubcomponent.Factory(UiScope::class)
    interface Factory {
        fun create(apiConfig: ApiConfig): UserComponent
    }
}
