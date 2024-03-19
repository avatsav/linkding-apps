package dev.avatsav.linkding.domain

import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Inspired by:
 * https://github.com/chrisbanes/tivi/blob/fb1e59a6c0244e378543bbbc761b26fbb61d896d/domain/src/commonMain/kotlin/app/tivi/domain/Interactor.kt#L69
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class Observer<P : Any, R> {

    private val paramState = MutableSharedFlow<P>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val flow: Flow<R> = paramState
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
        .distinctUntilChanged()

    suspend fun observe(params: P) {
        paramState.emit(params)
    }

    protected abstract fun createObservable(params: P): Flow<R>
}

abstract class PagedObserver<P : PagedObserver.Param<T>, T : Any> :
    Observer<P, PagingData<T>>() {

    interface Param<T : Any> {
        val pagingConfig: PagingConfig
    }
}
