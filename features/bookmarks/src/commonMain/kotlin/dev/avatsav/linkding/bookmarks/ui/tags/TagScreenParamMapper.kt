package dev.avatsav.linkding.bookmarks.ui.tags

import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreenParam

fun Tag.mapToScreenParam(): TagsScreenParam = TagsScreenParam(id, name)

fun TagsScreenParam.mapToTag(): Tag = Tag(id, name)
