package dev.avatsav.linkding.bookmarks.ui.util

import androidx.paging.compose.LazyPagingItems

/** Returns a snapshot list of all currently loaded, non-null items. */
fun <T : Any> LazyPagingItems<T>.loadedList(): List<T> =
  (0 until itemCount).mapNotNull { index -> this[index] }
