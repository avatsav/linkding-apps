package dev.avatsav.linkding.data.db.sqldelight.paging

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

import androidx.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterBase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.properties.Delegates

internal abstract class QueryPagingSource<Key : Any, RowType : Any> :
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

/**
 * Create a [PagingSource] that pages through results according to queries generated by
 * [queryProvider]. Queries returned by [queryProvider] should expect to do SQL offset/limit
 * based paging. For that reason, [countQuery] is required to calculate pages and page offsets.
 * [initialOffset] initial offset to start paging from.
 *
 * An example query returned by [queryProvider] could look like:
 *
 * ```sql
 * SELECT value FROM numbers
 * LIMIT 10
 * OFFSET 100;
 * ```
 *
 * Queries will be executed on [context].
 */
@Suppress("FunctionName")
@JvmName("QueryPagingSourceInt")
@JvmOverloads
fun <RowType : Any> QueryPagingSource(
    countQuery: Query<Int>,
    transacter: TransacterBase,
    context: CoroutineContext,
    queryProvider: (limit: Int, offset: Int) -> Query<RowType>,
    initialOffset: Int = 0,
): PagingSource<Int, RowType> = OffsetQueryPagingSource(
    queryProvider,
    countQuery,
    transacter,
    context,
    initialOffset,
)

/**
 * Variant of [QueryPagingSource] that accepts a [Long] instead of an [Int] for [countQuery]
 * , [queryProvider] and [initialOffset].
 *
 * If the result of [countQuery] exceeds [Int.MAX_VALUE], then the count will be truncated
 * to the least significant 32 bits of this [Long] value.
 *
 * @see toInt
 */
@Suppress("FunctionName")
@JvmName("QueryPagingSourceLong")
@JvmOverloads
fun <RowType : Any> QueryPagingSource(
    countQuery: Query<Long>,
    transacter: TransacterBase,
    context: CoroutineContext,
    queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    initialOffset: Long = 0,
): PagingSource<Int, RowType> = OffsetQueryPagingSource(
    { limit, offset -> queryProvider(limit.toLong(), offset.toLong()) },
    countQuery.toInt(),
    transacter,
    context,
    initialOffset.toInt(),
)

private fun Query<Long>.toInt(): Query<Int> =
    object : Query<Int>({ cursor -> mapper(cursor).toInt() }) {
        override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>) = this@toInt.execute(mapper)
        override fun addListener(listener: Listener) = this@toInt.addListener(listener)
        override fun removeListener(listener: Listener) = this@toInt.removeListener(listener)
    }