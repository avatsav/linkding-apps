package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.ContributesSubcomponent
import com.slack.circuit.foundation.Circuit
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.inject.qualifier.Authenticated

@ContributesSubcomponent(
    scope = UserScope::class,
    parentScope = UiScope::class,
)
@SingleIn(UserScope::class)
interface UserComponent {

    @Authenticated
    val circuit: Circuit

    @ContributesSubcomponent.Factory
    interface Factory {
        fun create(apiConfig: ApiConfig): UserComponent
    }
}
