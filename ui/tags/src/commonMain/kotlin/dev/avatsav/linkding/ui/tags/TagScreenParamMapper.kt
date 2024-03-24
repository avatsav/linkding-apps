package dev.avatsav.linkding.ui.tags

import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreenParam

fun Tag.mapToScreenParam(): TagsScreenParam {
    return TagsScreenParam(id, name)
}

fun TagsScreenParam.mapToTag(): Tag {
    return Tag(id, name)
}
