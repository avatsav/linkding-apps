// Copyright 2021, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
inline fun <T : Any> Flow<PagingData<T>>.rememberCachedPagingFlow(
    scope: CoroutineScope = rememberStableCoroutineScope(),
): Flow<PagingData<T>> = remember(this, scope) { cachedIn(scope) }
