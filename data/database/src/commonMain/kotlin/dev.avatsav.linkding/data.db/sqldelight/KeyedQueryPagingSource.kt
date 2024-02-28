/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.avatsav.linkding.data.db.sqldelight

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultInvalid
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacter
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterBase
import app.cash.sqldelight.TransactionCallbacks
import dev.avatsav.linkding.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates
import kotlinx.coroutines.withContext

private abstract class QueryPagingSource<Key : Any, RowType : Any> :
    PagingSource<Key, RowType>(),
    Query.Listener {

    protected var currentQuery: Query<RowType>? by Delegates.observable(null) { _, old, new ->
        old?.removeListener(this)
        new?.addListener(this)
    }

    init {
        registerInvalidatedCallback {
            currentQuery?.removeListener(this)
            currentQuery = null
        }
    }

    final override fun queryResultsChanged() = invalidate()
}

private class KeyedQueryPagingSource<Key : Any, RowType : Any>(
    private val queryProvider: (beginInclusive: Key, endExclusive: Key?) -> Query<RowType>,
    private val pageBoundariesProvider: (anchor: Key?, limit: Long) -> Query<Key>,
    private val transacter: TransacterBase,
    private val context: CoroutineContext,
    private val logger: Logger,
) : QueryPagingSource<Key, RowType>() {

    private var pageBoundaries: List<Key>? = null
    override val jumpingSupported: Boolean get() = false

    override fun getRefreshKey(state: PagingState<Key, RowType>): Key? {
        val boundaries = pageBoundaries ?: return null
        val last = state.pages.lastOrNull() ?: return null
        val keyIndexFromNext = last.nextKey?.let { boundaries.indexOf(it) - 1 }
        val keyIndexFromPrev = last.prevKey?.let { boundaries.indexOf(it) + 1 }
        val keyIndex = keyIndexFromNext ?: keyIndexFromPrev ?: return null

        return boundaries.getOrNull(keyIndex)
    }

    override suspend fun load(params: PagingSourceLoadParams<Key>): PagingSourceLoadResult<Key, RowType> =
        withContext(context) {
            try {
                val getPagingSourceLoadResult: TransactionCallbacks.() -> PagingSourceLoadResult<Key, RowType> =
                    {
                        val boundaries = pageBoundaries
                            ?: pageBoundariesProvider(params.key, params.loadSize.toLong())
                                .executeAsList()
                                .also { pageBoundaries = it }

                        // If the boundaries are empty, that means that we have no elements in the table.
                        // We cannot really proceed with calling the queryProvider for results and thus
                        // cannot rely on the QueryPagingSource to invalidate the PagingSource on changes.
                        if (boundaries.isEmpty()) {
                            // Instead of error, we'll return an Invalid result when the boundaries are empty.
                            PagingSourceLoadResultInvalid<Int, RowType>() as PagingSourceLoadResult<Key, RowType>
                        } else {
                            val key = params.key ?: boundaries.first()
                            require(key in boundaries)
                            val keyIndex = boundaries.indexOf(key)
                            val previousKey = boundaries.getOrNull(keyIndex - 1)
                            val nextKey = boundaries.getOrNull(keyIndex + 1)
                            val results = queryProvider(key, nextKey)
                                .also { currentQuery = it }
                                .executeAsList()
                            PagingSourceLoadResultPage(
                                data = results,
                                prevKey = previousKey,
                                nextKey = nextKey,
                            ) as PagingSourceLoadResult<Key, RowType>
                        }
                    }
                when (transacter) {
                    is Transacter -> transacter.transactionWithResult(bodyWithReturn = getPagingSourceLoadResult)
                    is SuspendingTransacter -> transacter.transactionWithResult(bodyWithReturn = getPagingSourceLoadResult)
                }
            } catch (e: Exception) {
                logger.e(e) { "KeyedQueryPagingSource:loadError ${e.message}" }
                if (e is IllegalArgumentException) throw e
                PagingSourceLoadResultError<Key, RowType>(e) as PagingSourceLoadResult<Key, RowType>
            }
        }
}

@Suppress("FunctionName")
fun <Key : Any, RowType : Any> QueryPagingSource(
    transacter: TransacterBase,
    context: CoroutineContext,
    pageBoundariesProvider: (anchor: Key?, limit: Long) -> Query<Key>,
    queryProvider: (beginInclusive: Key, endExclusive: Key?) -> Query<RowType>,
    logger: Logger,
): PagingSource<Key, RowType> = KeyedQueryPagingSource(
    queryProvider,
    pageBoundariesProvider,
    transacter,
    context,
    logger,
)
