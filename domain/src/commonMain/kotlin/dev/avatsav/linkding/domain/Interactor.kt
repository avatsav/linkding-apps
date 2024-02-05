package dev.avatsav.linkding.domain

import com.github.michaelbull.result.Result
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

abstract class Interactor<P, R, E> {

    private val count = atomic(0)
    private val loadingState = MutableStateFlow(count.value)

    val inProgress: Flow<Boolean> = loadingState.map { it > 0 }.distinctUntilChanged()

    suspend operator fun invoke(
        param: P,
    ): Result<R, E> = try {
        addLoader()
        doWork(param)
    } finally {
        removeLoader()
    }

    protected abstract suspend fun doWork(param: P): Result<R, E>

    private fun addLoader() {
        loadingState.value = count.incrementAndGet()
    }

    private fun removeLoader() {
        loadingState.value = count.decrementAndGet()
    }
}
