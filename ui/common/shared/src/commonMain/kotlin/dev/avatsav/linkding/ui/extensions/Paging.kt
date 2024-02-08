package dev.avatsav.linkding.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
inline fun <T : Any> Flow<PagingData<T>>.rememberCachedPagingFlow(
    scope: CoroutineScope = rememberCoroutineScope(),
): Flow<PagingData<T>> = remember(this, scope) { cachedIn(scope) }
