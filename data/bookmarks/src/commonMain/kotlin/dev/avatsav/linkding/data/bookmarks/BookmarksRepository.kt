package dev.avatsav.linkding.data.bookmarks

import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.api.Linkding
import dev.avatsav.linkding.data.db.BookmarksDao

class BookmarksRepository(
    private val linkding: Linkding,
    private val bookmarksDao: BookmarksDao,
    private val dispatchers: AppCoroutineDispatchers,
) {





}
