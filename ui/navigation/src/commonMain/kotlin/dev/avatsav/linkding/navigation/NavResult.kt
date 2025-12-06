package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Immutable

/** Marker interface for navigation results. */
@Immutable interface NavResult

/** Marker interface for screens that return a typed result. */
interface ScreenWithResult<R : NavResult>
