package dev.avatsav.linkding.domain.observers

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.domain.Observer
import dev.avatsav.linkding.prefs.AppPreferences
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveApiConfiguration(
    private val prefs: AppPreferences,
) : Observer<Unit, ApiConfig?>() {

    override fun createObservable(params: Unit): Flow<ApiConfig?> =
        prefs.observeApiConfiguration()
}
