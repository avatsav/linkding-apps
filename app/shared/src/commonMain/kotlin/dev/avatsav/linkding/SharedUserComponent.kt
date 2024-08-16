package dev.avatsav.linkding

import dev.avatsav.linkding.data.bookmarks.inject.LinkdingComponent
import dev.avatsav.linkding.ui.add.inject.AddBookmarkComponent
import dev.avatsav.linkding.ui.bookmarks.inject.BookmarksComponent
import dev.avatsav.linkding.ui.settings.inject.SettingsComponent
import dev.avatsav.linkding.ui.tags.inject.TagsComponent

interface SharedUserComponent :
    LinkdingComponent,
    BookmarksComponent,
    AddBookmarkComponent,
    SettingsComponent,
    TagsComponent
