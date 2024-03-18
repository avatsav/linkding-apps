// Copyright (C) 2023 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

// https://github.com/slackhq/circuit/blob/7b4050f5c7f767591d7dbc2390f519b0ab89f24d/circuit-runtime/src/commonMain/kotlin/com/slack/circuit/runtime/internal/StableCoroutineScope.kt

/**
 * Returns a [StableCoroutineScope] around a [rememberCoroutineScope]. This is useful for event
 * callback lambdas that capture a local scope variable to launch new coroutines, as it allows them
 * to be stable.
 */
@Composable
fun rememberStableCoroutineScope(): StableCoroutineScope {
    val scope = rememberCoroutineScope()
    return remember { StableCoroutineScope(scope) }
}

/** @see rememberStableCoroutineScope */
@Stable
class StableCoroutineScope(scope: CoroutineScope) : CoroutineScope by scope
