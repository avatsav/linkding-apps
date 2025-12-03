package dev.avatsav.linkding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

typealias ScreenEntryProviderScope = EntryProviderScope<NavKey>.() -> Unit
