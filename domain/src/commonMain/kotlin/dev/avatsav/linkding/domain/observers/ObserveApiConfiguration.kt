package dev.avatsav.linkding.domain.observers

import dev.avatsav.linkding.data.config.ApiConfigRepository
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.domain.Observer
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveApiConfiguration(
    private val repository: ApiConfigRepository,
) : Observer<Unit, ApiConfig?>() {

    override fun createObservable(params: Unit): Flow<ApiConfig?> =
        repository.observeApiConfiguration()
}
