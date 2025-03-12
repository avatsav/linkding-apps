// Copyright 2018, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.domain

import com.github.michaelbull.result.Result
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * https://github.com/chrisbanes/tivi/blob/fb1e59a6c0244e378543bbbc761b26fbb61d896d/domain/src/commonMain/kotlin/app/tivi/domain/Interactor.kt
 */
abstract class Interactor<P, R, E> {

  private val count = atomic(0)
  private val loadingState = MutableStateFlow(count.value)

  val inProgress: Flow<Boolean> = loadingState.map { it > 0 }.distinctUntilChanged()

  suspend operator fun invoke(param: P): Result<R, E> =
    try {
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
