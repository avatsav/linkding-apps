/*
 * Copyright 2025 The Android Open Source Project
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
package dev.avatsav.linkding.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.RetainedValuesStore
import androidx.compose.runtime.retain.RetainedValuesStoreRegistry
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
/**
 * Returns a [RetainedValuesStoreNavEntryDecorator] that is remembered across recompositions backed
 * by [registry].
 *
 * The underlying storage is controlled by the provided [registry]. By default, a new
 * [RetainedValuesStoreRegistry] is retained at this point in the composition hierarchy and will be
 * destroyed when the composition is permanently discarded or when the returned decorator is removed
 * from the composition hierarchy. If you need the backing storage of this decorator to have a
 * different lifespan, you can manually manage and provide a [RetainedValuesStoreRegistry] with the
 * intended lifespan.
 *
 * @param registry The underlying [RetainedValuesStoreRegistry] used to provide
 *   [RetainedValuesStore] instances to [NavEntries][NavEntry]. This instance should be retained to
 *   properly survive destruction and recreation scenarios.
 * @param removeRetainedValuesStoreOnPop A lambda that returns a Boolean indicating whether the
 *   [RetainedValuesStore] for a [NavEntry] should be removed when the [NavEntry] is popped from the
 *   back stack. If true, the entry's RetainedValuesStore will be removed, retiring all retained
 *   values for the removed content.
 */
@Composable
public fun <T : Any> rememberRetainedValuesStoreNavEntryDecorator(
  registry: RetainedValuesStoreRegistry = retainRetainedValuesStoreRegistry(),
  removeRetainedValuesStoreOnPop: () -> Boolean =
    RetainedValuesStoreNavEntryDecoratorDefaults.removeRetainedValuesStoreOnPop(),
): RetainedValuesStoreNavEntryDecorator<T> {
  val currentRemoveStoreOnPop = rememberUpdatedState(removeRetainedValuesStoreOnPop)
  return remember(registry) {
    RetainedValuesStoreNavEntryDecorator(registry) { currentRemoveStoreOnPop.value.invoke() }
  }
}
/**
 * Provides the content of each [NavEntry] with a dedicated [RetainedValuesStore] so that each nav
 * entry may retain its own values.
 *
 * @param registry The underlying [RetainedValuesStoreRegistry] used to provide
 *   [RetainedValuesStore] instances to [NavEntries][NavEntry]. This instance should be retained to
 *   properly survive destruction and recreation scenarios.
 * @param removeStoreOnPop A lambda that returns a Boolean indicating whether the
 *   [RetainedValuesStore] for a [NavEntry] should be removed when the [NavEntry] is popped from the
 *   back stack. If true, the entry's RetainedValuesStore will be removed, retiring all retained
 *   values for the removed content.
 */
public class RetainedValuesStoreNavEntryDecorator<T : Any>(
  registry: RetainedValuesStoreRegistry,
  removeStoreOnPop: () -> Boolean,
) :
  NavEntryDecorator<T>(
    onPop = { key ->
      if (removeStoreOnPop()) {
        registry.clearChild(key)
      }
    },
    decorate = { entry ->
      registry.LocalRetainedValuesStoreProvider(entry.contentKey) { entry.Content() }
    },
  )
/** Defines the default arguments for [RetainedValuesStoreNavEntryDecorator]. */
public expect object RetainedValuesStoreNavEntryDecoratorDefaults {
  /**
   * Controls whether the [RetainedValuesStoreNavEntryDecorator] should free the
   * [RetainedValuesStore] scoped to a [NavEntry] when [NavEntryDecorator.onPop] is invoked for
   * that [NavEntry]'s [NavEntry.contentKey]
   *
   * The [RetainedValuesStore] is disposed if this returns true. The store is retained if false.
   */
  @Composable public fun removeRetainedValuesStoreOnPop(): () -> Boolean
}
