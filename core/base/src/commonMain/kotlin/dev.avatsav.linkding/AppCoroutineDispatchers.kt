package dev.avatsav.linkding

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

data class AppCoroutineDispatchers(
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)

typealias AppCoroutineScope = CoroutineScope
